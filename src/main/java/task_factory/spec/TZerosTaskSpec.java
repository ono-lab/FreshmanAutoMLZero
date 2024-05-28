package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TZerosTaskCreator;

public class TZerosTaskSpec extends TTaskSpec {
  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TZerosTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
