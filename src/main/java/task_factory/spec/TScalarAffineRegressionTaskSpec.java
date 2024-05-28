package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TScalarAffineRegressionTaskCreator;

public class TScalarAffineRegressionTaskSpec extends TTaskSpec {
  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TScalarAffineRegressionTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
