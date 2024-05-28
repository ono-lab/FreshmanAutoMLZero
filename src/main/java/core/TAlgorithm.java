package core;

import java.util.ArrayList;
import java.util.Objects;

public class TAlgorithm {
  private ArrayList<TInstruction> fSetup = new ArrayList<TInstruction>();
  private ArrayList<TInstruction> fPredict = new ArrayList<TInstruction>();
  private ArrayList<TInstruction> fLearn = new ArrayList<TInstruction>();

  private TDependentVariables fPredictDependentVariables = new TDependentVariables();
  private TDependentVariables fLearnDependentVariables = new TDependentVariables();
  private TDependentVariables fPredictLearnDependentVariables = new TDependentVariables();
  private TDependentVariables fSetupDependentVariables = new TDependentVariables();

  private TRequiredVariables fPredictRequiredVariables = new TRequiredVariables();
  private TRequiredVariables fLearnRequiredVariables = new TRequiredVariables();

  private ArrayList<TVariable> fLearningPrams = new ArrayList<TVariable>();

  /**
   * アルゴリズムを生成するコンストラクタ
   */
  public TAlgorithm() {}

  /**
   * srcのコンポーネントをdestにcopyする関数
   */
  public static void copyComponent(ArrayList<TInstruction> src, ArrayList<TInstruction> dest) {
    dest.clear();
    for (TInstruction instr : src) {
      dest.add(instr.clone());
    }
  }

  public TAlgorithm copyFrom(TAlgorithm other) {
    if (other != this) {
      copyComponent(other.fSetup, fSetup);
      copyComponent(other.fPredict, fPredict);
      copyComponent(other.fLearn, fLearn);
      fPredictDependentVariables.addAll(other.fPredictDependentVariables.getVariables());
      fPredictLearnDependentVariables.addAll(other.fPredictLearnDependentVariables.getVariables());
      fLearnDependentVariables.addAll(other.fLearnDependentVariables.getVariables());
      fSetupDependentVariables.addAll(other.fSetupDependentVariables.getVariables());
      fPredictRequiredVariables.copyFrom(other.fPredictRequiredVariables);
      fLearnRequiredVariables.copyFrom(other.fLearnRequiredVariables);
      fLearningPrams.addAll(other.fLearningPrams);
    }
    return this;
  }

  public TAlgorithm(TAlgorithm other) {
    copyFrom(other);
  }

  @Override
  public TAlgorithm clone() {
    return new TAlgorithm(this);
  }

  public ArrayList<TInstruction> getSetup() {
    return fSetup;
  }

  public ArrayList<TInstruction> getPredict() {
    return fPredict;
  }

  public ArrayList<TInstruction> getLearn() {
    return fLearn;
  }

  /**
   * 指定されたコンポーネント（setup, predict, learnのいずれか）を返却する関数
   */
  public ArrayList<TInstruction> getComponent(TComponentType componentType) {
    switch (componentType) {
      case SETUP:
        return fSetup;
      case PREDICT:
        return fPredict;
      case LEARN:
        return fLearn;
      default:
        throw new RuntimeException("Should not reach here.");
    }
  }

  public TDependentVariables getSetupDependentVariables() {
    return fSetupDependentVariables;
  }

  public TDependentVariables getPredictDependentVariables() {
    return fPredictDependentVariables;
  }
  
  public TDependentVariables getLearnDependentVariables() {
    return fLearnDependentVariables;
  }

  public TDependentVariables getPredictLearnDependentVariables() {
    return fPredictLearnDependentVariables;
  }

  public TRequiredVariables getPredictRequiredVariables() {
    return fPredictRequiredVariables;
  }

  public TRequiredVariables getLearnRequiredVariables() {
    return fLearnRequiredVariables;
  }

  public ArrayList<TVariable> getLearningParams() {
    return fLearningPrams;
  }

  public void setLearningParams(ArrayList<TVariable> newParams) {
    fLearningPrams = newParams;
  }

  /**
   * アルゴリズムとして等しいかの真偽を返却する関数
   */
  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (!(other instanceof TAlgorithm))
      return false;
    TAlgorithm otherAlgorithm = (TAlgorithm) other;
    if (!Objects.equals(fSetup, otherAlgorithm.fSetup))
      return false;
    if (!Objects.equals(fPredict, otherAlgorithm.fPredict))
      return false;
    if (!Objects.equals(fLearn, otherAlgorithm.fLearn))
      return false;
    return true;
  }

  @Override
  public String toString() {
    String str = "";
    str += "def Setup():\n";
    for (TInstruction instr : fSetup)
      str += instr + "\n";
    str += "\ndef Predict():\n";
    for (TInstruction instr : fPredict)
      str += instr + "\n";
    str += "\ndef Learn():\n";
    for (TInstruction instr : fLearn)
      str += instr + "\n";
    return str;
  }
}
