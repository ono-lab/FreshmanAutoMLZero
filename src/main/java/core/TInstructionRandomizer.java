package core;

import utils.TRandomGenerator;

/**
 * 命令のパラメータを書き換える際に用いるクラス．インスタンス化はされない．
 */
public class TInstructionRandomizer {
  /**
   * 命令をコピーしてパラメータをランダムに書き換える関数
   */
  static public TInstruction copyInstructionAndAlterParam(final TInstruction other,
      final TRandomGenerator rand) {
    final TInstruction instruction = new TInstruction();
    instruction.copyFrom(other);
    alterParam(instruction, rand);
    return instruction;
  }

  /**
   * 指定された命令でパラメータをランダムに初期化した命令を生成する関数
   */
  static public TInstruction makeInstructionAndRandomize(final TOp op,
      final TRandomGenerator rand) {
    final TInstruction instruction = new TInstruction();
    setOpAndRandomize(instruction, op, rand);
    return instruction;
  }
  
  static public void setOpAndRandomize(final TInstruction instruction, final TOp op,
      final TRandomGenerator rand) {
    setOpAndRandomize(instruction, op, rand, false);
  }

  /**
   * 指定された命令をセットして，パラメータをランダムに初期化する関数
   */
  static public void setOpAndRandomize(final TInstruction instruction, final TOp op,
      final TRandomGenerator rand, boolean isPredict) {
    instruction.fillWithNoOp();
    instruction.setOp(op);
    switch (op) {
      case NO_OP:
        return;
      case SCALAR_CONST_SET_OP:
      case VECTOR_CONST_SET_OP:
      case MATRIX_CONST_SET_OP:
      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        randomizeOut(instruction, rand);
        randomizeData(instruction, rand);
        return;
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
        randomizeIn1(instruction, rand, isPredict);
        randomizeOut(instruction, rand);
        return;
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
        randomizeIn1(instruction, rand, isPredict);
        randomizeIn2(instruction, rand, isPredict);
        randomizeOut(instruction, rand);
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  static public void alterParam(final TInstruction instruction, final TRandomGenerator rand) {
    alterParam(instruction, rand, false);
  }

  /**
   * １つのパラメータをランダムに選択して，ランダムに値をセットする関数
   */
  static public void alterParam(final TInstruction instruction, final TRandomGenerator rand, final boolean isPredict) {
    switch (instruction.getOp()) {
      case NO_OP:
        return;
      case SCALAR_CONST_SET_OP:
      case VECTOR_CONST_SET_OP:
      case MATRIX_CONST_SET_OP:
      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        switch (rand.nextChoice2()) {
          case 0:
            randomizeOut(instruction, rand);
            return;
          case 1:
            alterData(instruction, rand);
            return;
        }
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
        switch (rand.nextChoice2()) {
          case 0:
            randomizeIn1(instruction, rand, isPredict);
            return;
          case 1:
            randomizeOut(instruction, rand);
            return;
        }
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
        switch (rand.nextChoice3()) {
          case 0:
            randomizeIn1(instruction, rand, isPredict);
            return;
          case 1:
            randomizeIn2(instruction, rand, isPredict);
            return;
          case 2:
            randomizeOut(instruction, rand);
            return;
        }
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  static public void randomizeIn1(final TInstruction instruction, final TRandomGenerator rand) {
    randomizeIn1(instruction, rand, false);
  }

  /**
   * In1をランダムに初期化する関数
   */
  static public void randomizeIn1(final TInstruction instruction, final TRandomGenerator rand,
      final boolean isPredict) {
    switch (instruction.getOp()) {
      case NO_OP:
      case SCALAR_CONST_SET_OP:
      case VECTOR_CONST_SET_OP:
      case MATRIX_CONST_SET_OP:
      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        throw new RuntimeException("Invalid TOp.");
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
        instruction
            .setIn1(isPredict ? rand.nextScalarInAddressOfPredict() : rand.nextScalarInAddress());
        return;
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case VECTOR_NORM_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case VECTOR_RECIPROCAL_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
        instruction.setIn1(rand.nextVectorInAddress());
        return;
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
        instruction.setIn1(rand.nextMatrixInAddress());
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }
  
  static public void randomizeIn2(final TInstruction instruction, final TRandomGenerator rand) {
    randomizeIn2(instruction, rand, false);
  }

  /**
   * In2をランダムに初期化する関数
   */
  static public void randomizeIn2(final TInstruction instruction, final TRandomGenerator rand, final boolean isPredict) {
    switch (instruction.getOp()) {
      case NO_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_CONST_SET_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case VECTOR_CONST_SET_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case MATRIX_CONST_SET_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        throw new RuntimeException("Invalid TOp.");
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
        instruction
            .setIn2(isPredict ? rand.nextScalarInAddressOfPredict() : rand.nextScalarInAddress());
        return;
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
        instruction.setIn2(rand.nextVectorInAddress());
        return;
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
        instruction.setIn2(rand.nextMatrixInAddress());
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  /**
   * firstInputsとsecondInputが入力として使われるように、命令の入力を設定する。firstInputsやsecondInputがnullになっている場合は、ランダムに入力が設定される。
   * @param instruction
   * @param firstInput
   * @param secondInput
   * @param rand
   */
  static public void randomizeOrSetInputs(final TInstruction instruction, TVariable firstInput,  TVariable secondInput,
      final TRandomGenerator rand, final boolean isPredict) {
    TOp op = instruction.getOp();
    TMemoryType firstInputMemoryType = firstInput != null ? firstInput.getMemoryType() : null;
    TMemoryType secondInputMemoryType = secondInput != null ? secondInput.getMemoryType() : null;
    if (firstInput != null && secondInput != null) {
      if (firstInputMemoryType == secondInputMemoryType) {
        switch (rand.nextChoice2()) {
          case 0:
            instruction.setIn1(firstInput.getAddress());
            instruction.setIn2(secondInput.getAddress());
            break;
          case 1:
            instruction.setIn1(secondInput.getAddress());
            instruction.setIn2(firstInput.getAddress());
            break;
        }
      } else if (TOp.getIn1MemoryType(op) == firstInputMemoryType) {
        instruction.setIn1(firstInput.getAddress());
        instruction.setIn2(secondInput.getAddress());
      } else if (TOp.getIn2MemoryType(op) == firstInputMemoryType) {
        instruction.setIn1(secondInput.getAddress());
        instruction.setIn2(firstInput.getAddress());
      } else {
        throw new RuntimeException("Unexpected error has occurred.");
      }
    } else if (firstInput != null) {
      if (firstInputMemoryType == secondInputMemoryType) {
        switch (rand.nextChoice2()) {
          case 0:
            instruction.setIn1(firstInput.getAddress());
            TInstructionRandomizer.randomizeIn2(instruction, rand, isPredict);
            break;
          case 1:
            instruction.setIn2(firstInput.getAddress());
            TInstructionRandomizer.randomizeIn1(instruction, rand, isPredict);
            break;
        }
      } else if (TOp.getIn1MemoryType(op) == firstInputMemoryType) {
        instruction.setIn1(firstInput.getAddress());
        TInstructionRandomizer.randomizeIn2(instruction, rand, isPredict);
      } else if (TOp.getIn2MemoryType(op) == firstInputMemoryType) {
        instruction.setIn2(firstInput.getAddress());
        TInstructionRandomizer.randomizeIn1(instruction, rand, isPredict);
      } else {
        throw new RuntimeException("Unexpected error has occurred.");
      }
    } else {
      TInstructionRandomizer.randomizeIn1(instruction, rand, isPredict);
      TInstructionRandomizer.randomizeIn2(instruction, rand, isPredict);
    }
  }

  /**
   * Outをランダムに初期化する関数
   */
  static public void randomizeOut(final TInstruction instruction, final TRandomGenerator rand) {
    switch (instruction.getOp()) {
      case NO_OP:
        throw new RuntimeException("Invalid TOp.");
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_CONST_SET_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case SCALAR_GAUSSIAN_SET_OP:
      case SCALAR_UNIFORM_SET_OP:
        instruction.setOut(rand.nextScalarOutAddress());
        return;
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case VECTOR_CONST_SET_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
        instruction.setOut(rand.nextVectorOutAddress());
        return;
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case MATRIX_CONST_SET_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_TRANSPOSE_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
      case MATRIX_GAUSSIAN_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
      case MATRIX_RECIPROCAL_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
        instruction.setOut(rand.nextMatrixOutAddress());
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  /**
   * Dataをランダムに初期化する関数
   */
  static public void randomizeData(final TInstruction instruction, final TRandomGenerator rand) {
    randomizeData(instruction, rand, true);
  }

  /**
   * 対応可能な命令の場合にDataをランダムに初期化する関数
   */
  static public void maybeRandomizeData(final TInstruction instruction, final TRandomGenerator rand) {
    randomizeData(instruction, rand, false);
  }

  /**
   * Dataをランダムに初期化する関数, throwErrorをfalseに設定すると対応していない命令はスルーする
   */
  static private void randomizeData(final TInstruction instruction, final TRandomGenerator rand, boolean throwError) {
    switch (instruction.getOp()) {
      case NO_OP:
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
        if (throwError)
          throw new RuntimeException("Invalid TOp.");
        else
          return;
      case SCALAR_CONST_SET_OP: {
        instruction.setActivationData(rand.nextDouble(-1.0, 1.0));
        return;
      }
      case VECTOR_CONST_SET_OP: {
        // FloatData0 represents the index. See FloatToIndex for more details.
        instruction.setFloatData0(rand.nextFloat(0.0f, 1.0f));
        // FloatData1 represents the value to store.
        instruction.setFloatData1(rand.nextFloat(-1.0f, 1.0f));
        return;
      }
      case MATRIX_CONST_SET_OP: {
        instruction.setFloatData0(rand.nextFloat(0.0f, 1.0f));
        instruction.setFloatData1(rand.nextFloat(0.0f, 1.0f));
        instruction.setFloatData2(rand.nextFloat(-1.0f, 1.0f));
        return;
      }
      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP: {
        instruction.setFloatData0(rand.nextFloat(-1.0f, 1.0f)); // Mean.
        instruction.setFloatData1(rand.nextFloat(0.0f, 1.0f));// St. dev.
        return;
      }
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP: {
        instruction.setFloatData0(rand.nextFloat(-1.0f, 1.0f)); // Mean.
        instruction.setFloatData1(rand.nextFloat(-1.0f, 1.0f)); // St. dev
        return;
      }
      default:
        if (throwError)
          throw new RuntimeException("Invalid TOp.");
        else
          return;
    }
  }

  static private double mutateActivationLogScale(final TRandomGenerator rand, final double value) {
    if (value > 0) {
      return Math.exp(Math.log(value) + rand.nextGaussian(0.0, 1.0));
    } else {
      return -Math.exp(Math.log(-value) + rand.nextGaussian(0.0, 1.0));
    }
  }

  static private float mutateFloatLogScale(final TRandomGenerator rand, final float value) {
    if (value > 0) {
      Double db = Math.exp(Math.log(value) + rand.nextGaussian(0.0, 1.0));
      return db.floatValue();
    } else {
      Double db = -Math.exp(Math.log(-value) + rand.nextGaussian(0.0, 1.0));
      return db.floatValue();
    }
  }

  static private final double kSignFlipProb = 0.1;

  static private double mutateActivationLogScaleOrFlip(final TRandomGenerator rand, double value) {
    if (rand.nextProbability() < kSignFlipProb) {
      value = -value;
    } else {
      value = mutateActivationLogScale(rand, value);
    }
    return value;
  }

 static private float mutateFloatLogScaleOrFlip(final TRandomGenerator rand, float value) {
    if (rand.nextProbability() < kSignFlipProb) {
      value = -value;
    } else {
      value = mutateFloatLogScale(rand, value);
    }
    return value;
  }

  /**
   * Dataの一部をランダムに変更する関数
   */
  static public void alterData(final TInstruction instruction, final TRandomGenerator rand) {
    switch (instruction.getOp()) {
      case NO_OP:
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
        throw new RuntimeException("Invalid TOp.");
      case SCALAR_CONST_SET_OP:
        instruction.setActivationData(
            mutateActivationLogScaleOrFlip(rand, instruction.getActivationData()));
        return;
      case VECTOR_CONST_SET_OP:
        switch (rand.nextChoice2()) {
          case 0:
            // Mutate index. See FloatToIndex for more details.
            instruction.setFloatData0(rand.nextFloat(0.0f, 1.0f));
            break;
          case 1:
            // Mutate value.
            instruction.setFloatData1(mutateFloatLogScaleOrFlip(rand, instruction.getFloatData1()));
            break;
        }
        return;
      case MATRIX_CONST_SET_OP:
        switch (rand.nextChoice3()) {
          case 0:
            // Mutate first index.
            instruction.setFloatData0(rand.nextFloat(0.0f, 1.0f));
            break;
          case 1:
            // Mutate second index.
            instruction.setFloatData1(rand.nextFloat(0.0f, 1.0f));
            break;
          case 2:
            // Mutate value.
            instruction.setFloatData2(mutateFloatLogScaleOrFlip(rand, instruction.getFloatData2()));
            break;
        }
        return;

      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
        switch (rand.nextChoice2()) {
          case 0:
            // Mutate mean.
            instruction.setFloatData0(mutateFloatLogScaleOrFlip(rand, instruction.getFloatData0()));
            break;
          case 1:
            // Mutate stdev.
            instruction.setFloatData1(mutateFloatLogScale(rand, instruction.getFloatData1()));
            break;
        }
        return;
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        float value0 = instruction.getFloatData0();
        float value1 = instruction.getFloatData1();
        switch (rand.nextChoice2()) {
          case 0:
            // Mutate low.
            value0 = mutateFloatLogScaleOrFlip(rand, value0);
            break;
          case 1:
            // Mutate high.
            value1 = mutateFloatLogScaleOrFlip(rand, value1);
            break;
        }
        instruction.setFloatData0(Math.min(value0, value1));
        instruction.setFloatData1(Math.max(value0, value1));
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  /**
   * Dataの一部をランダムに変更する関数
   */
  static public void randomizeDataExceptIndex(final TInstruction instruction, final TRandomGenerator rand, boolean initialize) {
    switch (instruction.getOp()) {
      case NO_OP:
      case SCALAR_SUM_OP:
      case SCALAR_DIFF_OP:
      case SCALAR_PRODUCT_OP:
      case SCALAR_DIVISION_OP:
      case SCALAR_MIN_OP:
      case SCALAR_MAX_OP:
      case SCALAR_ABS_OP:
      case SCALAR_HEAVYSIDE_OP:
      case SCALAR_SIN_OP:
      case SCALAR_COS_OP:
      case SCALAR_TAN_OP:
      case SCALAR_ARCSIN_OP:
      case SCALAR_ARCCOS_OP:
      case SCALAR_ARCTAN_OP:
      case SCALAR_EXP_OP:
      case SCALAR_LOG_OP:
      case VECTOR_SUM_OP:
      case VECTOR_DIFF_OP:
      case VECTOR_PRODUCT_OP:
      case VECTOR_DIVISION_OP:
      case VECTOR_MIN_OP:
      case VECTOR_MAX_OP:
      case VECTOR_ABS_OP:
      case VECTOR_HEAVYSIDE_OP:
      case MATRIX_SUM_OP:
      case MATRIX_DIFF_OP:
      case MATRIX_PRODUCT_OP:
      case MATRIX_DIVISION_OP:
      case MATRIX_MIN_OP:
      case MATRIX_MAX_OP:
      case MATRIX_ABS_OP:
      case MATRIX_HEAVYSIDE_OP:
      case SCALAR_VECTOR_PRODUCT_OP:
      case VECTOR_INNER_PRODUCT_OP:
      case VECTOR_OUTER_PRODUCT_OP:
      case SCALAR_MATRIX_PRODUCT_OP:
      case MATRIX_VECTOR_PRODUCT_OP:
      case VECTOR_NORM_OP:
      case MATRIX_NORM_OP:
      case MATRIX_TRANSPOSE_OP:
      case MATRIX_MATRIX_PRODUCT_OP:
      case VECTOR_MEAN_OP:
      case VECTOR_ST_DEV_OP:
      case MATRIX_MEAN_OP:
      case MATRIX_ST_DEV_OP:
      case MATRIX_ROW_MEAN_OP:
      case MATRIX_ROW_ST_DEV_OP:
      case SCALAR_RECIPROCAL_OP:
      case SCALAR_BROADCAST_OP:
      case VECTOR_RECIPROCAL_OP:
      case MATRIX_RECIPROCAL_OP:
      case MATRIX_ROW_NORM_OP:
      case MATRIX_COLUMN_NORM_OP:
      case VECTOR_COLUMN_BROADCAST_OP:
      case VECTOR_ROW_BROADCAST_OP:
        throw new RuntimeException("Invalid TOp.");
      case SCALAR_CONST_SET_OP:
        instruction.setActivationData(
            initialize ? rand.nextDouble(-1.0, 1.0) : mutateActivationLogScaleOrFlip(rand, instruction.getActivationData()));
        return;
      case VECTOR_CONST_SET_OP:
        // Mutate value.
        instruction.setFloatData1(initialize ? rand.nextFloat(-1.0f, 1.0f) : mutateFloatLogScaleOrFlip(rand, instruction.getFloatData1()));
        return;
      case MATRIX_CONST_SET_OP:
        instruction.setFloatData2(initialize ? rand.nextFloat(-1.0f, 1.0f) : mutateFloatLogScaleOrFlip(rand, instruction.getFloatData2()));
        return;

      case SCALAR_GAUSSIAN_SET_OP:
      case VECTOR_GAUSSIAN_SET_OP:
      case MATRIX_GAUSSIAN_SET_OP:
        switch (rand.nextChoice2()) {
          case 0:
            // Mutate mean.
            instruction.setFloatData0(initialize ? rand.nextFloat(-1.0f, 1.0f)
                : mutateFloatLogScaleOrFlip(rand, instruction.getFloatData0()));
            break;
          case 1:
            // Mutate stdev.
            instruction.setFloatData1(initialize ? rand.nextFloat(-1.0f, 1.0f)
                : mutateFloatLogScale(rand, instruction.getFloatData1()));
            break;
        }
        return;
      case SCALAR_UNIFORM_SET_OP:
      case VECTOR_UNIFORM_SET_OP:
      case MATRIX_UNIFORM_SET_OP:
        float value0 = instruction.getFloatData0();
        float value1 = instruction.getFloatData1();
        switch (rand.nextChoice2()) {
          case 0:
            // Mutate low.
            value0 = mutateFloatLogScaleOrFlip(rand, value0);
            break;
          case 1:
            // Mutate high.
            value1 = mutateFloatLogScaleOrFlip(rand, value1);
            break;
        }
        instruction.setFloatData0(Math.min(value0, value1));
        instruction.setFloatData1(Math.max(value0, value1));
        return;
      default:
        throw new RuntimeException("invalid op.");
    }
  }
}
