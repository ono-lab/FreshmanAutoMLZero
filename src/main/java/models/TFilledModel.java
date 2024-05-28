package models;

import core.TAlgorithm;
import core.TInstruction;

/**
 * A Algorithm with specific instruction.
 */
public class TFilledModel extends TModel {
  private TInstruction fInstruction;

  public TFilledModel(TInstruction instruction, int setupInitSize, int predictInitSize,
      int learnInitSize) {
    super(setupInitSize, predictInitSize, learnInitSize);
    fInstruction = instruction;
  }

  @Override
  protected TAlgorithm getImpl() {
    TAlgorithm algorithm = new TAlgorithm();
    fillComponentWithInstruction(algorithm.getSetup(), getSetupInitSize(), fInstruction);
    fillComponentWithInstruction(algorithm.getPredict(), getPredictInitSize(), fInstruction);
    fillComponentWithInstruction(algorithm.getLearn(), getLearnInitSize(), fInstruction);
    return algorithm;
  }
}
