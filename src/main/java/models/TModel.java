package models;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
abstract public class TModel {
  protected static final TInstruction kNoOpInstruction = new TInstruction();

  protected int fSetupInitSize = 0;
  protected int fPredictInitSize = 0;
  protected int fLearnInitSize = 0;

  TModel() {}

  TModel(int setupInitSize, int predictInitSize, int learnInitSize) {
    fSetupInitSize = setupInitSize;
    fPredictInitSize = predictInitSize;
    fLearnInitSize = learnInitSize;
  }

  protected int getSetupInitSize() {
    return fSetupInitSize;
  }

  protected int getPredictInitSize() {
    return fPredictInitSize;
  }

  protected int getLearnInitSize() {
    return fLearnInitSize;
  }

  protected static void fillComponentWithInstruction(ArrayList<TInstruction> component,
      int numOfInstructions, TInstruction instr) {
    component.clear();
    for (int index = 0; index < numOfInstructions; index++) {
      component.add(instr.clone());
    }
  }

  protected static void padComponentWithInstruction(ArrayList<TInstruction> component,
      int totalNumOfInstructions, TInstruction instr) {
    while (component.size() < totalNumOfInstructions) {
      component.add(instr.clone());
    }
  }

  abstract protected TAlgorithm getImpl();

  public TAlgorithm get() {
    TAlgorithm algorithm = getImpl();
    padComponentWithInstruction(algorithm.getSetup(), fSetupInitSize, kNoOpInstruction);
    padComponentWithInstruction(algorithm.getPredict(), fPredictInitSize, kNoOpInstruction);
    padComponentWithInstruction(algorithm.getLearn(), fLearnInitSize, kNoOpInstruction);
    return algorithm;
  };

  public void initialize(int seed) {}
}
