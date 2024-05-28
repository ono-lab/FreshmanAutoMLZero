package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TScalar2LayerNNRegressionTaskCreator;

public class TScalar2LayerNNRegressionTaskSpec extends TTaskSpec {
  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TScalar2LayerNNRegressionTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
