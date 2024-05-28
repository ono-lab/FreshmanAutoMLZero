package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TScalarLinearRegressionTaskCreator;

public class TScalarLinearRegressionTaskSpec extends TTaskSpec {
  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TScalarLinearRegressionTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
