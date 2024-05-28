package core;

import java.util.ArrayList;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import utils.TMathUtility;
import utils.TRandomGenerator;

public class TAlgorithmExecutor {
  // Fitness bounds.
  static final double kMinFitness = 0.0;
  static final double kMaxFitness = 1.0;

  // The Algorithm being trained.
  private TAlgorithm fAlgorithm;

  // The dataset used for training.
  private TTask fDataset;

  // The following values can be assigned independently of the dataset (auto
  // adjustment)
  private int fNumOfAllTrainExamples; // Includes the examples in all the training epochs.
  private int fNumOfAllValidExamples;

  private TRandomGenerator fRand;
  private TMemory fMemory;

  // Errors larger than this trigger early stopping, as they signal
  // models that likely have runaway behavior. Early stopping can also
  // be triggered if the loss for an example is infinite, nan, or too
  // large. If early stopping is triggered, the fitness for the
  // execution will be set to the minimum value.
  private double fMaxAbsError;

  private long fNumOfTrainCompletedSteps = 0;

  // Constructs a standard executor. Uses a clean memory and automatically
  // executes the setup component function. All arguments are stored by
  // reference, so they must out-live the Executor instance.
  public TAlgorithmExecutor(final TAlgorithm algorithm, final TMemory memory, final TTask dataset,
      final int numOfAllTrainExamples, final int numOfAllValidExamples, final double maxAbsError,
      final TRandomGenerator rand) {
    assert dataset.getFeatureSize() == memory.getDim();
    fAlgorithm = algorithm;
    fDataset = dataset;
    fNumOfAllTrainExamples = numOfAllTrainExamples;
    fNumOfAllValidExamples = numOfAllValidExamples;
    fMemory = memory;
    fMaxAbsError = maxAbsError;
    fRand = rand;
    setUp();
  };

  public long getNumOfTrainCompletedSteps() {
    return fNumOfTrainCompletedSteps;
  }

  static private void setUp(TAlgorithm algorithm, TMemory memory, TRandomGenerator rand) {
    memory.wipe();
    for (TInstruction instruction : algorithm.getSetup()) {
      TInstructionExecutor.execute(instruction, rand, memory);
    }
  }

  private void setUp() {
    setUp(fAlgorithm, fMemory, fRand);
  }

  static private double predict(TAlgorithm algorithm, TEvalMethod evalMethod, TMemory memory,
      TRandomGenerator rand, TCMatrix feature) {
    memory.setFeatureVector(feature.clone());
    memory.resetLabel();
    for (TInstruction instruction : algorithm.getPredict()) {
      TInstructionExecutor.execute(instruction, rand, memory);
    }
    if (evalMethod == TEvalMethod.ACCURACY) {
      memory.convertPredictionToProbability();
    }
    return memory.getPredictionScalar();
  }

  private double predict(TCMatrix feature) {
    return predict(fAlgorithm, fDataset.getEvalType(), fMemory, fRand, feature);
  }

  static private void learn(TAlgorithm algorithm, TMemory memory, TRandomGenerator rand,
      TCMatrix feature, double label) {
    memory.setFeatureVector(feature.clone());
    memory.setLabelScalar(label);
    for (TInstruction instruction : algorithm.getLearn()) {
      TInstructionExecutor.execute(instruction, rand, memory);
    }
  }

  private void learn(TCMatrix feature, double label) {
    learn(fAlgorithm, fMemory, fRand, feature, label);
  }

  private boolean train(int maxSteps, TTaskIterator trainIterator, ArrayList<Double> errors) {
    for (int step = 0; step < maxSteps; step++) {
      fNumOfTrainCompletedSteps++;

      // Run predict component function for this example.
      TCMatrix feature = trainIterator.getFeature();
      double prediction = predict(feature);

      // Check whether we should stop early.
      double label = trainIterator.getLabel();
      double absError = Math.abs(prediction - label);
      if (Double.isInfinite(absError) || Double.isNaN(absError) || absError >= fMaxAbsError) {
        return false;
      }
      if (errors != null) {
        errors.add(absError);
      }

      // Run learn component function for this example.
      learn(feature, label);

      // Check whether we are done.
      trainIterator.next();
      if (trainIterator.hasDone()) {
        break; // Reached the end of the dataset.
      }
    }
    return true;
  }

  private double validate(ArrayList<Double> errors) {
    double loss = 0.0;
    int numOfSteps = Math.min(fNumOfAllValidExamples, fDataset.getNumOfValidExamples());
    TTaskIterator validIterator = fDataset.getValidIterator();
    for (int step = 0; step < numOfSteps; step++) {
      TCMatrix feature = validIterator.getFeature();
      double prediction = predict(feature);

      // Accumulate the loss.
      double error = Double.NaN;
      double label = validIterator.getLabel();
      switch (fDataset.getEvalType()) {
        case RMS_ERROR:
          error = label - prediction;
          loss += error * error;
          break;
        case ACCURACY:
          // The prediction value is probability if EvalMethod is ACCURACY
          if (prediction > 1.0 || prediction < 0.0) {
            error = Double.MAX_VALUE;
          } else {
            boolean isCorrect = (prediction > 0.5) == (label > 0.5);
            error = isCorrect ? 0.0 : 1.0;
          }
          loss += error;
          break;
        default:
          throw new RuntimeException("Invalid eval type.");
      }
      double absError = Math.abs(error);
      if (Double.isInfinite(absError) || Double.isNaN(absError) || absError >= fMaxAbsError) {
        // Stop early. Return infinite loss.
        return kMinFitness;
      }
      if (errors != null) {
        errors.add(error);
      }

      // Check whether we are done.
      validIterator.next();
      if (validIterator.hasDone()) {
        break; // Reached the end of the dataset.
      }
    }

    switch (fDataset.getEvalType()) {
      case RMS_ERROR:
        loss /= fDataset.getNumOfValidExamples();
        loss = TMathUtility.squash(Math.sqrt(loss));
        break;
      case ACCURACY:
        loss /= fDataset.getNumOfValidExamples();
        break;
      default:
        throw new RuntimeException("Invalid eval type.");
    }

    double fitness = 1 - loss;
    assert fitness <= kMaxFitness;
    assert fitness >= kMinFitness;
    return fitness;
  }


  /**
   * アルゴリズムを実行し，最も良かったエポックのFitnessを返却する．
   * 学習中のErrorはtrainErrorsに，検証中のエラーはvalidErrorsに格納される．ただし，格納は第一エポックのみである．
   */
  public double execute(ArrayList<Double> trainErrors, ArrayList<Double> validErrors) {
    assert fDataset.getNumOfTrainEpochs() >= 1;
    assert trainErrors == null || trainErrors.size() == 0;
    assert validErrors == null || validErrors.size() == 0;

    // Iterators that track the progress of training.
    TTaskIterator trainIterator = fDataset.getTrainIterator();

    // Train for multiple epochs, evaluate on validation set
    // after each epoch and take the best validation result as fitness.
    int numOfAllTrainExamples =
        Math.min(fNumOfAllTrainExamples, fDataset.getNumOfMaxTrainExamples());
    int numOfTrainExamplesPerEpoch =
        Math.min(numOfAllTrainExamples, fDataset.getNumOfTrainExamplesPerEpoch());
    int numOfRemaining = numOfAllTrainExamples;

    double bestFitness = kMinFitness;
    while (numOfRemaining > 0) {
      boolean isSuccess =
          train(Math.min(numOfTrainExamplesPerEpoch, numOfRemaining), trainIterator, trainErrors);
      if (!isSuccess) {
        if (numOfRemaining == numOfAllTrainExamples)
          return kMinFitness;
        else
          break;
      }
      numOfRemaining -= numOfTrainExamplesPerEpoch;
      double currentFitness = validate(validErrors);
      bestFitness = Math.max(currentFitness, bestFitness);
      // Only save the errors of the first epoch.
      if (trainErrors != null) {
        trainErrors = null;
        validErrors = null;
      }
    }
    return bestFitness;
  }

  public double execute() {
    return execute(null, null);
  }

  public static void executeAndFillLabels(TAlgorithm algorithm, TMemory memory, TTaskBuffer buffer,
      TRandomGenerator rand) {
    // Fill training labels.
    for (int example = 0; example < buffer.getTrainDataSize(); example++) {
      TCMatrix feature = buffer.getTrainFeatures().get(example);
      // Run predict component function for this example.
      double label = predict(algorithm, buffer.getEvalMethod(), memory, rand, feature);
      buffer.setTrainLabel(example, label);
    }

    // Fill validation labels.
    for (int example = 0; example < buffer.getValidDataSize(); example++) {
      TCMatrix feature = buffer.getValidFeatures().get(example);
      // Run predict component function for this example.
      double label = predict(algorithm, buffer.getEvalMethod(), memory, rand, feature);
      buffer.setValidLabel(example, label);
    }
  }
}
