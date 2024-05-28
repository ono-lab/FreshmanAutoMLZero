package task_factory.creator;

import core.*;
import models.*;
import task_factory.spec.TScalar2LayerNNRegressionTaskSpec;
import utils.TRandomGenerator;
import jp.ac.titech.onolab.core.matrix.TCMatrix;

public class TScalar2LayerNNRegressionTaskCreator {
  // Creates a task using the nonlinear regressor with fixed weights. The
  // weights are determined by the seed. Serves as a way to initialize the
  // task.
  public static TTaskBuffer execute(TScalar2LayerNNRegressionTaskSpec spec, long paramSeed,
      long dataSeed) {
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;
    TEvalMethod evalMethod = spec.evalMethod;

    assert featureSize > 0;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;
    assert evalMethod == TEvalMethod.RMS_ERROR;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(evalMethod);

    TRandomGenerator dataRand = new TRandomGenerator(dataSeed + 865546086);

    // Fill the features.
    for (int example = 0; example < numOfTrainExamples; example++) {
      TCMatrix feature = new TCMatrix(featureSize);
      dataRand.fillGaussian(0.0, 1.0, feature);
      buffer.addTrainData(feature);
    }
    for (int example = 0; example < numOfValidExamples; example++) {
      TCMatrix feature = new TCMatrix(featureSize);
      dataRand.fillGaussian(0.0, 1.0, feature);
      buffer.addValidData(feature);
    }

    // Create a Algorithm and memory deterministically.
    TAlgorithm algorithm = new TNeuralNetNoBiasNoGradientModel(0.0).get();
    TMemory memory = new TMemory(featureSize);

    TRandomGenerator weightsRand = new TRandomGenerator(paramSeed + 174299604);

    // wipe and setup
    memory.wipe();
    weightsRand.fillGaussian(0.0, 1.0,
        memory.matrix[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFirstLayerWeightsAddress]);
    for (int col = 0; col < featureSize; col++) {
      memory.matrix[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFirstLayerWeightsAddress]
          .setValue(0, col, 0.0);
      memory.matrix[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFirstLayerWeightsAddress]
          .setValue(2, col, 0.0);
    }
    weightsRand.fillGaussian(0.0, 1.0,
        memory.vector[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFinalLayerWeightsAddress]);
    memory.vector[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFinalLayerWeightsAddress]
        .setValue(0, 0.0);
    memory.vector[TNeuralNetNoBiasNoGradientModel.kUnitTestNeuralNetNoBiasNoGradientFinalLayerWeightsAddress]
        .setValue(2, 0.0);

    // Fill in the labels by executing the Algorithm.
    TAlgorithmExecutor.executeAndFillLabels(algorithm, memory, buffer, dataRand);

    return buffer;
  }
}
