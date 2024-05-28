package task_factory.creator;

import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import task_factory.spec.TIncrementTaskSpec;

public class TIncrementTaskCreator {
  public static TTaskBuffer execute(TIncrementTaskSpec spec, long paramSeed, long dataSeed) {
    double increment = spec.increment;
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;

    assert featureSize > 0;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(spec.evalMethod);

    double incrementingScalar = increment;
    double incrementedScalar = 0.0;
    TCMatrix incrementedVector = new TCMatrix(buffer.getFeatureSize()).fill(0.0);
    TCMatrix incrementingVector = new TCMatrix(buffer.getFeatureSize()).fill(incrementingScalar);
    for (int example = 0; example < numOfTrainExamples; example++) {
      buffer.addTrainData(incrementedVector.clone(), incrementedScalar);
      incrementedVector.add(incrementingVector);
      incrementedScalar += incrementingScalar;
    }
    incrementedScalar = 0.0;
    incrementedVector.fill(0.0);
    for (int example = 0; example < numOfValidExamples; example++) {
      buffer.addValidData(incrementedVector.clone(), incrementedScalar);
      incrementedVector.add(incrementingVector);
      incrementedScalar += incrementingScalar;
    }

    return buffer;
  }
}
