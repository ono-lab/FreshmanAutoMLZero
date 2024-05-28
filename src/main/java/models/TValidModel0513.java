package models;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.TAlgorithm;
import core.TAllowedOpsManager;
import core.TDependentVariables;
import core.TInstruction;
import core.TInstructionExecutor;
import core.TInstructionRandomizer;
import core.TMemoryType;
import core.TOp;
import core.TRequiredVariables;
import core.TVariable;
import utils.TArrayUtility;
import utils.TRandomGenerator;

class NotFoundOpException extends RuntimeException {
  public NotFoundOpException() {
    super("Not found the op matches condition.");
  }
}

class NoOutputVariablesException extends RuntimeException {
  public NoOutputVariablesException() {
    super("No variables are used for output.");
  }
}

public class TValidModel0513 extends TModel {
  private TAllowedOpsManager fAllowedOpsManager;
  private TRandomGenerator fRand;
  private long fSeed;
  private int fMaxNumOfLearningParams;

  @JsonCreator
  public TValidModel0513(@JsonProperty("setupInitSize") int setupInitSize,
      @JsonProperty("predictInitSize") int predictInitSize,
      @JsonProperty("learnInitSize") int learnInitSize, @JsonProperty("setupOps") TOp[] setupOps,
      @JsonProperty("predictOps") TOp[] predictOps, @JsonProperty("learnOps") TOp[] learnOps,
      @JsonProperty("maxNumOfLearningParams") int maxNumOfLearningParams, @JsonProperty("seed") long seed) {
    super(setupInitSize, predictInitSize, learnInitSize);
    fAllowedOpsManager = new TAllowedOpsManager(setupOps, predictOps, learnOps);
    fRand = new TRandomGenerator(seed);
    fMaxNumOfLearningParams = maxNumOfLearningParams;
    fSeed = seed;
  }

  private TDependentVariables configurePredict(TAlgorithm algorithm) {
    ArrayList<TInstruction> predict = algorithm.getPredict();
    TRequiredVariables requiredVariables = algorithm.getPredictRequiredVariables();
    requiredVariables.initialize(fPredictInitSize);
    TDependentVariables dependentVariables = algorithm.getPredictDependentVariables();

    // 最後の出力はs1
    dependentVariables.add(new TVariable(1, TMemoryType.SCALAR));

    // V0は必ず使用する
    TVariable v0 = new TVariable(0, TMemoryType.VECTOR);
    Integer v0Index = requiredVariables.randomAssignVariable(fRand, v0);

    assert v0Index != null;

    ArrayList<TVariable> forbidden = new ArrayList<TVariable>();

    TVariable s0 = new TVariable(0, TMemoryType.SCALAR);
    TVariable s1 = new TVariable(1, TMemoryType.SCALAR);

    forbidden.add(v0);
    forbidden.add(s0);
    forbidden.add(s1);    

    for (int index = 0; index < fPredictInitSize; index++) {
      if (dependentVariables.isBlank()) {
        throw new NoOutputVariablesException();
      }

      // 依存関係の解析結果と事前に決定された入力の条件を満たすように命令を決定
      TVariable firstInput = requiredVariables.get(index, 1);
      TMemoryType firstInputMemoryType = firstInput != null ? firstInput.getMemoryType() : null;
      TVariable secondInput = requiredVariables.get(index, 2);
      TMemoryType secondInputMemoryType = secondInput != null ? secondInput.getMemoryType() : null;
      TOp op = fAllowedOpsManager.getMemoryTypeDesignatedRandomPredictOp(
          dependentVariables.getAvailableMemoryTypes(), firstInputMemoryType, secondInputMemoryType,
          fRand, null);

      if (op == null)
        throw new NotFoundOpException();

      // 命令を作成して追加
      TInstruction instruction = new TInstruction();
      instruction.setOp(op);
      TVariable out = dependentVariables.randomRemove(fRand, TOp.getOutMemoryType(op));
      instruction.setOut(out.getAddress());
      TInstructionRandomizer.randomizeOrSetInputs(instruction, firstInput, secondInput, fRand, true);
      TInstructionRandomizer.maybeRandomizeData(instruction, fRand);
      predict.add(0, instruction);

      // 依存関係を更新
      TVariable input1Variable = instruction.getInput1Variable();
      TVariable input2Variable = instruction.getInput2Variable();
      requiredVariables.fill(index);
      if (!TArrayUtility.include(forbidden, input1Variable)) {
        dependentVariables.add(instruction.getInput1Variable());
      }
      if (!TArrayUtility.include(forbidden, input2Variable)) {
        dependentVariables.add(instruction.getInput2Variable());
      }
    }
    return dependentVariables;
  }

  private TDependentVariables configureLearn(TAlgorithm algorithm) {
    ArrayList<TInstruction> learn = algorithm.getLearn();
    TDependentVariables learnDependentVariables = algorithm.getLearnDependentVariables();
    TDependentVariables predictDependentVariables = algorithm.getPredictLearnDependentVariables();
    predictDependentVariables.addAll(algorithm.getPredictDependentVariables().getVariables());
    if (predictDependentVariables.isBlank()) {
      throw new NoOutputVariablesException();
    }
    TRequiredVariables requiredVariables = algorithm.getLearnRequiredVariables();
    requiredVariables.initialize(fLearnInitSize);
    ArrayList<TVariable> copiedPredictDependentVariables =
        predictDependentVariables.copyVariables();

    TVariable v0 = new TVariable(0, TMemoryType.VECTOR);
    TVariable s0 = new TVariable(0, TMemoryType.SCALAR);
    TVariable s1 = new TVariable(1, TMemoryType.SCALAR);

    Integer v0Index = requiredVariables.randomAssignVariable(fRand, v0);
    assert v0Index != null;
    Integer s0Index = requiredVariables.randomAssignVariable(fRand, s0);
    assert s0Index != null;
    Integer s1Index = requiredVariables.randomAssignVariable(fRand, s1);
    assert s1Index != null;

    ArrayList<TVariable> forbidden = new ArrayList<TVariable>();
    forbidden.add(v0);
    forbidden.add(s0);
    forbidden.add(s1);

    int learningParamsCount = 0;
    ArrayList<TVariable> learningParams = new ArrayList<TVariable>();
    for (int index = 0; index < fLearnInitSize; index++) {
      // 依存関係の解析結果と事前に決定された入力の条件を満たすように命令を決定
      TVariable firstInput = requiredVariables.get(index, 1);
      TMemoryType firstInputMemoryType = firstInput != null ? firstInput.getMemoryType() : null;
      TVariable secondInput = requiredVariables.get(index, 2);
      TMemoryType secondInputMemoryType = secondInput != null ? secondInput.getMemoryType() : null;

      TOp op;
      TVariable out;
      if (requiredVariables.hasAvailableIndex() && learningParamsCount < fMaxNumOfLearningParams) {
        if (predictDependentVariables.isBlank()) {
          throw new NoOutputVariablesException();
        }
        ArrayList<TMemoryType> availableMemoryTypes =
            predictDependentVariables.getAvailableMemoryTypes();
        op = fAllowedOpsManager.getMemoryTypeDesignatedRandomLearnOp(availableMemoryTypes,
            firstInputMemoryType, secondInputMemoryType, fRand, null);
        if (op == null)
          throw new NotFoundOpException();
        out = predictDependentVariables.randomRemove(fRand, TOp.getOutMemoryType(op));
        learnDependentVariables.remove(out);
        if (TArrayUtility.include(copiedPredictDependentVariables, out)) {
          // 今の命令の条件でoutを入力に割り当てないようであればrequiredVariablesを埋める
          // つまり、割り当てられないときはoutが使われるのは本命令より前で使われることになる。
          if (!TOp.isAssignable(op, firstInput, secondInput, out)) {
            requiredVariables.fill(index);
          }
          Integer availableIndex = requiredVariables.randomAssignVariable(fRand, out);
          if (availableIndex != null) {
            // もし今の命令の入力に割り当てられているようであればinputを更新しておく。
            if (availableIndex == index) {
              if (firstInput == null) {
                firstInput = out;
                firstInputMemoryType = out.getMemoryType();
              } else if (secondInput == null) {
                secondInput = out;
                secondInputMemoryType = out.getMemoryType();
              } else {
                throw new RuntimeException("Unexpected error has occurred.");
              }
            }
            learningParams.add(out);
            forbidden.add(out);
            learningParamsCount++;
          } else {
            assert index != 0;
            if (learnDependentVariables.isBlank()) {
              throw new NoOutputVariablesException();
            }
            ArrayList<TMemoryType> learnAvailableMemoryTypes =
                learnDependentVariables.getAvailableMemoryTypes();
            op = fAllowedOpsManager.getMemoryTypeDesignatedRandomLearnOp(
                learnAvailableMemoryTypes,
                firstInputMemoryType, secondInputMemoryType, fRand, null);
            if (op == null)
              throw new NotFoundOpException();
            out = learnDependentVariables.randomRemove(fRand, TOp.getOutMemoryType(op));
            predictDependentVariables.remove(out);
          }
        }
      } else {
        assert index != 0;
        if (learnDependentVariables.isBlank()) {
          throw new NoOutputVariablesException();
        }
        ArrayList<TMemoryType> availableMemoryTypes =
            learnDependentVariables.getAvailableMemoryTypes();
        op = fAllowedOpsManager.getMemoryTypeDesignatedRandomLearnOp(availableMemoryTypes,
            firstInputMemoryType, secondInputMemoryType, fRand, null);
        if (op == null)
          throw new NotFoundOpException();
        out = learnDependentVariables.randomRemove(fRand, TOp.getOutMemoryType(op));
        predictDependentVariables.remove(out);
      }

      // 命令を作成して追加
      TInstruction instruction = new TInstruction();
      instruction.setOp(op);
      instruction.setOut(out.getAddress());
      TInstructionRandomizer.randomizeOrSetInputs(instruction, firstInput, secondInput, fRand, false);
      TInstructionRandomizer.maybeRandomizeData(instruction, fRand);
      learn.add(0, instruction);

      // 依存関係を更新
      TVariable input1Variable = instruction.getInput1Variable();
      TVariable input2Variable = instruction.getInput2Variable();
      requiredVariables.fill(index);
      if (!TArrayUtility.include(forbidden, input1Variable)) {
        predictDependentVariables.add(instruction.getInput1Variable());
        learnDependentVariables.add(instruction.getInput1Variable());
      }
      if (!TArrayUtility.include(forbidden, input2Variable)) {
        predictDependentVariables.add(instruction.getInput2Variable());
        learnDependentVariables.add(instruction.getInput2Variable());
      }
    }

    algorithm.setLearningParams(learningParams);

    return learnDependentVariables;
  }

  private void configureSetup(TAlgorithm algorithm, TDependentVariables setupDependentVariables) {
    ArrayList<TInstruction> setup = algorithm.getSetup();
    int dim = TInstruction.kFeatureSize;
    int index = 0;
    while (index < fSetupInitSize) {
      TOp op = fAllowedOpsManager.getMemoryTypeDesignatedRandomSetupOp(
          setupDependentVariables.getAvailableMemoryTypes(), fRand);
      if (op == null)
        return;
      TVariable out = setupDependentVariables.randomRemove(fRand, TOp.getOutMemoryType(op));
      TInstruction instruction = new TInstruction();
      instruction.setOp(op);
      instruction.setOut(out.getAddress());
      switch (out.getMemoryType()) {
        case SCALAR: {
          TInstructionRandomizer.randomizeDataExceptIndex(instruction, fRand, true);
          setup.add(instruction);
          index++;
          break;
        }
        case VECTOR: {
          if (index > fSetupInitSize - dim)
            break;
          for (int i = 0; i < dim; i++) {
            TInstruction eachInstruction = instruction.clone();
            eachInstruction.setFloatData0(TInstructionExecutor.indexToFloat(i, dim));
            TInstructionRandomizer.randomizeDataExceptIndex(eachInstruction, fRand, true);
            setup.add(eachInstruction);
            index++;
            assert index <= fSetupInitSize;
          }
          break;
        }
        case MATRIX: {
          if (index > fSetupInitSize - dim * dim)
            break;
          for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
              TInstruction eachInstruction = instruction.clone();
              eachInstruction.setFloatData0(TInstructionExecutor.indexToFloat(i, dim));
              eachInstruction.setFloatData1(TInstructionExecutor.indexToFloat(j, dim));
              TInstructionRandomizer.randomizeDataExceptIndex(eachInstruction, fRand, true);
              setup.add(eachInstruction);
              index++;
              assert index <= fSetupInitSize;
            }
          }
          break;
        }
        default:
          throw new Error("invalid op.");
      }
    }
  }

  @Override
  protected TAlgorithm getImpl() {
    try {
      TAlgorithm algorithm = new TAlgorithm();
      TDependentVariables setupDependentVariables = algorithm.getSetupDependentVariables();
      TDependentVariables predictDependentVariables = configurePredict(algorithm);
      setupDependentVariables.addAll(predictDependentVariables.getVariables());
      TDependentVariables learnDependentVariables = configureLearn(algorithm);
      setupDependentVariables.addAll(learnDependentVariables.getVariables());
      configureSetup(algorithm, setupDependentVariables);
      return algorithm;
    } catch (NotFoundOpException e) {
      return getImpl();
    } catch (NoOutputVariablesException e) {
      return getImpl();
    }
  }

  @Override
  public void initialize(int seed) {
    fRand = new TRandomGenerator(fSeed + seed);
  }

  public static void main(String[] args) {
    TModel model =
        new TValidModel0513(6, 2, 6, new TOp[] {TOp.SCALAR_CONST_SET_OP, TOp.VECTOR_CONST_SET_OP},
            new TOp[] {TOp.VECTOR_INNER_PRODUCT_OP, TOp.SCALAR_SUM_OP}, new TOp[] {TOp.SCALAR_DIFF_OP,
                TOp.SCALAR_PRODUCT_OP, TOp.SCALAR_VECTOR_PRODUCT_OP, TOp.VECTOR_SUM_OP,
                TOp.SCALAR_SUM_OP},
            100000, 100000);
    for (int i = 0; i < 1000; i++) {
      TAlgorithm algorithm = model.get();
      System.out.println("+++++++++++++++++++++");
      System.out.println(algorithm);
      if (algorithm.getPredictDependentVariables().isBlank()) {
        throw new Error();
      }
    }
  }
}
