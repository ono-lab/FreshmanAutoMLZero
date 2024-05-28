package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TIncrementTaskCreator;

public class TIncrementTaskSpec extends TTaskSpec {
  public double increment = 1.0;

  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TIncrementTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
