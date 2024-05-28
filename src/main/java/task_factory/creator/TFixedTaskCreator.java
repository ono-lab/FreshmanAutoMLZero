package task_factory.creator;

import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import task_factory.spec.TFixedTaskSpec;

public class TFixedTaskCreator {
  public static TTaskBuffer execute(TFixedTaskSpec spec, long paramSeed, long dataSeed) {
    TCMatrix[] trainFeatures = spec.trainFeatures;
    TCMatrix[] validFeatures = spec.validFeatures;
    double[] trainLabels = spec.trainLabels;
    double[] validLabels = spec.validLabels;
    int featureSize = spec.featuresSize;

    assert featureSize > 0;
    assert trainFeatures.length == trainLabels.length;
    assert validFeatures.length == validLabels.length;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(spec.evalMethod);

    for (int trainIndex = 0; trainIndex < trainFeatures.length; trainIndex++) {
      assert trainFeatures[trainIndex].getDimension() == buffer.getFeatureSize();
      buffer.addTrainData(trainFeatures[trainIndex], trainLabels[trainIndex]);
    }
    for (int validIndex = 0; validIndex < validFeatures.length; validIndex++) {
      assert validFeatures[validIndex].getDimension() == buffer.getFeatureSize();
      buffer.addValidData(validFeatures[validIndex], validLabels[validIndex]);
    }

    return buffer;
  }
}
