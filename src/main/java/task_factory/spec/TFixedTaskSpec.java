package task_factory.spec;

import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import task_factory.creator.TFixedTaskCreator;

/*
 * A task where the data is specified explicitly during construction. Useful for unit tests.
 */
public class TFixedTaskSpec extends TTaskSpec {
  public TCMatrix[] trainFeatures;
  public double[] trainLabels;
  public TCMatrix[] validFeatures;
  public double[] validLabels;

  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TFixedTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
