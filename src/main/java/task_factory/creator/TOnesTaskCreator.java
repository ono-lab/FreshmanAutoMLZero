package task_factory.creator;

import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import task_factory.spec.TOnesTaskSpec;

public class TOnesTaskCreator {
  public static TTaskBuffer execute(TOnesTaskSpec spec, long paramSeed, long dataSeed) {
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;

    assert featureSize > 0;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(spec.evalMethod);

    TCMatrix onesVector = new TCMatrix(buffer.getFeatureSize()).fill(1.0);
    for (int example = 0; example < numOfTrainExamples; example++) {
      buffer.addTrainData(onesVector.clone(), 1.0);
    }
    for (int example = 0; example < numOfValidExamples; example++) {
      buffer.addValidData(onesVector.clone(), 1.0);
    }
    return buffer;
  }
}
