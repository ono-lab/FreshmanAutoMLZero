package task_factory.creator;

import models.*;
import task_factory.spec.TScalarAffineRegressionTaskSpec;
import utils.TRandomGenerator;
import core.*;
import jp.ac.titech.onolab.core.matrix.TCMatrix;

public class TScalarAffineRegressionTaskCreator {
  // Creates a task using the linear regressor with fixed weights. The
  // weights are determined by the seed. Serves as a way to initialize the
  // task.
  public static TTaskBuffer execute(TScalarAffineRegressionTaskSpec spec, long paramSeed,
      long dataSeed) {
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;
    TEvalMethod evalMethod = spec.evalMethod;

    assert featureSize > 0;
    assert evalMethod == TEvalMethod.RMS_ERROR;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(evalMethod);

    TRandomGenerator dataRand = new TRandomGenerator(dataSeed + 939723201);

    // Fill the features.
    for (int example = 0; example < numOfTrainExamples; example++) {
      TCMatrix feature = new TCMatrix(buffer.getFeatureSize());
      dataRand.fillGaussian(0.0, 1.0, feature);
      buffer.addTrainData(feature);
    }
    for (int example = 0; example < numOfValidExamples; example++) {
      TCMatrix feature = new TCMatrix(buffer.getFeatureSize());
      dataRand.fillGaussian(0.0, 1.0, feature);
      buffer.addValidData(feature);
    }

    TRandomGenerator rand = new TRandomGenerator(paramSeed + 997958712);

    // Create a Algorithm and memory deterministically.
    TAlgorithm algorithm = new TAffineModel(0.0).get();
    TMemory memory = new TMemory(buffer.getFeatureSize());
    memory.wipe();
    rand.fillGaussian(0.0, 1.0, memory.vector[TAffineModel.kLinearAlgorithmWeightsAddress]);
    memory.scalar[TAffineModel.kLinearAlgorithmInterceptAddress] = rand.nextGaussian(0.0, 1.0);

    // Fill in the labels by executing the Algorithm.
    TAlgorithmExecutor.executeAndFillLabels(algorithm, memory, buffer, dataRand);

    return buffer;
  }
}
