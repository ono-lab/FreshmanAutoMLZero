package core;

import java.util.ArrayList;
import utils.TArrayUtility;

public class TAlgorithmValidator {
  boolean[] fIsValidSetup = null;
  boolean[] fIsValidPredict = null;
  boolean[] fIsValidLearn = null;
  TVariable kV0 = new TVariable(0, TMemoryType.VECTOR);
  TVariable kS1 = new TVariable(1, TMemoryType.SCALAR);
  TVariable kS0 = new TVariable(0, TMemoryType.SCALAR);

  int fFailedCount = 0;
  int fSuccessCount = 0;

  public int getFailedCount() {
    return fFailedCount;
  }

  public int getSuccessCount() {
    return fSuccessCount;
  }

  public void resetCounters() {
    fFailedCount = 0;
    fSuccessCount = 0;
  }

  private void init(TAlgorithm algorithm) {
    fIsValidSetup = new boolean[algorithm.getSetup().size()];
    fIsValidPredict = new boolean[algorithm.getPredict().size()];
    fIsValidLearn = new boolean[algorithm.getLearn().size()];
    for (int index = 0; index < fIsValidSetup.length; index++) {
      fIsValidSetup[index] = false;
    }
    for (int index = 0; index < fIsValidPredict.length; index++) {
      fIsValidPredict[index] = false;
    }
    for (int index = 0; index < fIsValidLearn.length; index++) {
      fIsValidLearn[index] = false;
    }
  }

  private boolean hasInDeps(ArrayList<TVariable> deps, TVariable variable) {
    return TArrayUtility.find(deps, variable) != null;
  }

  private TVariable findDeps(ArrayList<TVariable> deps, TVariable variable) {
    return TArrayUtility.find(deps, variable);
  }

  private void removeFromDeps(ArrayList<TVariable> deps, TVariable variable) {
    deps.remove(variable);
  }

  private void addToDeps(ArrayList<TVariable> deps, TVariable variable) {
    deps.add(variable);
  }

  private void validateComponentInstruction(TComponentType componentType, int index) {
    switch (componentType) {
      case SETUP:
        fIsValidSetup[index] = true;
        break;
      case PREDICT:
        fIsValidPredict[index] = true;
        break;
      case LEARN:
        fIsValidLearn[index] = true;
        break;
    }
  }

  private boolean checkComponentInstructions() {
    for (int index = 0; index < fIsValidSetup.length; index++) {
      if (!fIsValidSetup[index])
        return false;
    }
    for (int index = 0; index < fIsValidPredict.length; index++) {
      if (!fIsValidPredict[index])
        return false;
    }
    for (int index = 0; index < fIsValidLearn.length; index++) {
      if (!fIsValidLearn[index])
        return false;
    }
    return true;
  }

  private void analyzeComponent(ArrayList<TVariable> deps, ArrayList<TInstruction> component,
      TComponentType validateComponentType) {
    for (int instrIndex = component.size() - 1; instrIndex >= 0; instrIndex--) {
      TInstruction instr = component.get(instrIndex);
      TVariable outVariable = instr.getOutputVariable();
      TVariable depsFoundVariable = findDeps(deps, outVariable);
      if (outVariable != null && depsFoundVariable != null) {
        if (validateComponentType != null)
          validateComponentInstruction(validateComponentType, instrIndex);
        if (outVariable.canOverwrite(depsFoundVariable)) {
          removeFromDeps(deps, depsFoundVariable);
        } else {
          depsFoundVariable.marge(outVariable);
        }
        TVariable in1Variable = instr.getInput1Variable();
        if (in1Variable != null && !hasInDeps(deps, in1Variable)) {
          addToDeps(deps, in1Variable);
        }
        TVariable in2Variable = instr.getInput2Variable();
        if (in2Variable != null && !hasInDeps(deps, in2Variable)) {
          addToDeps(deps, in2Variable);
        }
      }
    }
  }

  private boolean existsLearningTarget(ArrayList<TInstruction> learn,
      ArrayList<TInstruction> predict, ArrayList<TVariable> maybeLearningTargets) {
    ArrayList<TVariable> deps = new ArrayList<TVariable>();
    for (TVariable maybeLearningTarget : maybeLearningTargets) {
      deps.clear();
      deps.add(maybeLearningTarget);
      analyzeComponent(deps, learn, null);

      // LPV prev
      if (!hasInDeps(deps, maybeLearningTarget)) {
        continue;
      }

      // LPV s0
      if (!hasInDeps(deps, kS0)) {
        continue;
      }

      // LPV s1
      if (!hasInDeps(deps, kS1)) {
        continue;
      }

      removeFromDeps(deps, kS1);

      analyzeComponent(deps, predict, null);

      // s1以外のv0に依存パラメータに学習対象パラメータが依存していること
      // LPV v0
      if (!hasInDeps(deps, kV0)) {
        continue;
      }

      return true;
    }
    return false;
  }

  public boolean validate(TAlgorithm algorithm) {
    init(algorithm);
    ArrayList<TVariable> deps = new ArrayList<TVariable>();

    // PFV
    ArrayList<TInstruction> predict = algorithm.getPredict();
    TInstruction predictLastInstr = predict.get(predict.size() - 1);
    TVariable predictLastInstrOut = predictLastInstr.getOutputVariable();
    if (!predictLastInstrOut.equals(kS1)) {
      return false;
    }

    // 2回目のpredict
    addToDeps(deps, kS1); // 2回目のpredictの結果の依存関係を解析
    analyzeComponent(deps, algorithm.getPredict(), TComponentType.PREDICT);

    // PLV v0
    if (!hasInDeps(deps, kV0)) {
      fFailedCount++;
      return false;
    }
    removeFromDeps(deps, kV0);

    // 学習対象のデータに依存しているかの検証
    // PLV LP
    if (!existsLearningTarget(algorithm.getLearn(), algorithm.getPredict(), deps)) {
      fFailedCount++;
      return false;
    }

    // 以下は使われる前の再代入を検証する厳しい制約
    // この操作を導入すると検証に通るまで生成する時間が長くなり実験できないので一旦処理しない．
    // // 1回目のLearn
    // analyzeComponent(deps, algorithm.getLearn(), TComponentType.LEARN);
    // if (!hasInDeps(deps, kS0)) {
    // fFailedCount++;
    // return false;
    // }
    // removeFromDeps(deps, kS0);

    // // 1回目のPredict
    // analyzeComponent(deps, algorithm.getPredict(), TComponentType.PREDICT);
    // if (!hasInDeps(deps, kV0)) {
    // fFailedCount++;
    // return false;
    // }
    // removeFromDeps(deps, kV0);

    // // Setup
    // analyzeComponent(deps, algorithm.getSetup(), TComponentType.SETUP);

    // if (checkComponentInstructions()) {
    // fSuccessCount++;
    // return true;
    // } else {
    // fFailedCount++;
    // return false;
    // }

    fSuccessCount++;
    return true;
  }
}
