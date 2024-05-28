package task_factory.creator;

import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import task_factory.spec.TZerosTaskSpec;

public class TZerosTaskCreator {
  public static TTaskBuffer execute(TZerosTaskSpec spec, long paramSeed, long dataSeed) {
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;

    assert featureSize > 0;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(spec.evalMethod);

    TCMatrix zerosVector = new TCMatrix(buffer.getFeatureSize());
    for (int example = 0; example < numOfTrainExamples; example++) {
      buffer.addTrainData(zerosVector.clone(), 0.0);
    }
    for (int example = 0; example < numOfValidExamples; example++) {
      buffer.addValidData(zerosVector.clone(), 0.0);
    }

    return buffer;
  }
}
