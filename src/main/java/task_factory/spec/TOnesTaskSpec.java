package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TOnesTaskCreator;

public class TOnesTaskSpec extends TTaskSpec {
  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TOnesTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
