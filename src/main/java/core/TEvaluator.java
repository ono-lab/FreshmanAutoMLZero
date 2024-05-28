package core;

import java.util.ArrayList;
import task_factory.TTaskFactory;
import task_factory.spec.TTaskSpec;
import utils.THashGenerator;
import utils.TMathUtility;
import utils.TRandomGenerator;

public class TEvaluator {
  private static final int kMinNumOfTrainExamples = 10;
  private static final long kFECCacheRandomSeed = 235732282;

  private TFitnessCombinationMode fFitnessCombinationMode;

  // Contains only task specifications targeted to his worker.
  private TTaskSpec[] fTaskSpecs;

  private TTrainBudget fTrainBudget;

  private TMemory fMemory;

  private TRandomGenerator fRand;

  private ArrayList<TTask> fTasks = new ArrayList<TTask>();

  private TFECCache fFECCache;
  private TRandomGenerator fFECRand = new TRandomGenerator(kFECCacheRandomSeed);
  private ArrayList<Double> fFECTrainErrors = new ArrayList<Double>();
  private ArrayList<Double> fFECValidErrors = new ArrayList<Double>();

  private double fMaxAbsError;
  private long fNumOfTrainCompletedSteps = 0;

  public TEvaluator(final TFitnessCombinationMode fitnessCombinationMode,
      final TTaskSpec[] taskSpecs, final TFECCache FECCache, TTrainBudget trainBudget,
      final double maxAbsError, final TRandomGenerator rand) {
    fFitnessCombinationMode = fitnessCombinationMode;
    fTaskSpecs = taskSpecs;
    fFECCache = FECCache;
    fTrainBudget = trainBudget;
    fRand = rand;
    fMaxAbsError = maxAbsError;
    TTaskFactory.fillTasks(fTasks, fTaskSpecs);
    assert fTasks.size() > 0;
    fMemory = new TMemory(fTasks.get(0).getFeatureSize());
  }

  static double combineFitnesses(TFitnessCombinationMode mode, double[] fitnesses) {
    switch (mode) {
      case MEAN:
        return TMathUtility.mean(fitnesses);
      case MEDIAN:
        return TMathUtility.median(fitnesses);
      default:
        throw new RuntimeException("Unsupported fitness combination.");
    }
  }

  public double execute(TAlgorithm algorithm) {
    double[] fitnesses = new double[fTasks.size()];
    for (int taskIndex = 0; taskIndex < fTasks.size(); taskIndex++) {
      TTask task = fTasks.get(taskIndex);
      int numOfMaxTrainExamples = task.getNumOfMaxTrainExamples();
      assert numOfMaxTrainExamples >= kMinNumOfTrainExamples;
      // アルゴリズムがbudgetを超えてたら学習しない（numOfTrainExamples = 0）
      int numOfTrainExamples = fTrainBudget == null ? numOfMaxTrainExamples
          : fTrainBudget.getNumOfTrainExamplesInBudget(algorithm, numOfMaxTrainExamples);
      fitnesses[taskIndex] = executeForOneTask(algorithm, task, numOfTrainExamples);
    }
    double combinedFitnesses = combineFitnesses(fFitnessCombinationMode, fitnesses);
    return combinedFitnesses;
  }

  private double executeForOneTask(TAlgorithm algorithm, TTask task, int numOfTrainExamples) {
    if (fFECCache != null) {
      assert fFECCache.getNumOfTrainExamples() <= task.getNumOfMaxTrainExamples();
      assert fFECCache.getNumOfValidExamples() <= task.getNumOfValidExamples();
      if (fMemory.getDim() != task.getFeatureSize()) {
        fMemory.changeDim(task.getFeatureSize());
      }
      final TAlgorithmExecutor FECExecutor =
          new TAlgorithmExecutor(algorithm, fMemory, task, fFECCache.getNumOfTrainExamples(),
              fFECCache.getNumOfValidExamples(), fMaxAbsError, fFECRand);
      fFECTrainErrors.clear();
      fFECValidErrors.clear();
      FECExecutor.execute(fFECTrainErrors, fFECValidErrors);
      fNumOfTrainCompletedSteps += FECExecutor.getNumOfTrainCompletedSteps();
      final long hash = THashGenerator.getWellMixedHash(fFECTrainErrors, fFECValidErrors,
          task.getIndex(), numOfTrainExamples);
      final double cachedFitness = fFECCache.getCachedFitness(hash);

      // キャッシュがヒットした場合
      if (!Double.isNaN(cachedFitness)) {
        return cachedFitness;
      }
      // キャッシュがヒットしなかった場合
      else {
        final TAlgorithmExecutor executor = new TAlgorithmExecutor(algorithm, fMemory, task,
            numOfTrainExamples, task.getNumOfValidExamples(), fMaxAbsError, fRand);
        final double fitness = executor.execute();
        fNumOfTrainCompletedSteps += executor.getNumOfTrainCompletedSteps();
        fFECCache.put(hash, fitness); // キャッシュに書き込む
        return fitness;
      }
    } else {
      final TAlgorithmExecutor executor = new TAlgorithmExecutor(algorithm, fMemory, task,
          numOfTrainExamples, task.getNumOfValidExamples(), fMaxAbsError, fRand);
      final double fitness = executor.execute();
      fNumOfTrainCompletedSteps += executor.getNumOfTrainCompletedSteps();
      return fitness;
    }
  }

  public long getNumOfTrainCompletedSteps() {
    return fNumOfTrainCompletedSteps;
  }

  public void incrementFECGeneration() {
    if (fFECCache != null)
      fFECCache.incrementGeneration();
  }
}
