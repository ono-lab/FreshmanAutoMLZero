package core;

import core.instruction_data_setter.*;

/**
 * Tアルゴリズムの各関数内で使用する命令のクラス
 */
public class TInstruction {
  // toStringするときに必要となる次元（外部から指定可）
  public final static int kFeatureSize = 4;

  // 命令の種類
  private TOp fOp = TOp.NO_OP;

  // 1つめの引数のメモリアドレス
  private int fIn1 = 0;

  // 2つめの引数のメモリアドレス
  private int fIn2 = 0;

  // 出力先のメモリアドレス
  private int fOut = 0;

  // 以下はメモリ以外からメモリにデータを渡す際に使用する変数である．
  // 例えば，定数代入の定数部分，定数代入の場所を表すindex，正規乱数の平均や分散等
  // fActivationDataはスカラーの定数代入のみで使用していて，
  // 学習率等で精度が重要となる場合が想定されるためdouble型となっている．
  private double fActivationData = 0.0;
  private float fFloatData0 = 0.0f;
  private float fFloatData1 = 0.0f;
  private float fFloatData2 = 0.0f;

  public TInstruction() {}

  public TInstruction(final TDataSetter<Integer> integerDataSetter) {
    fActivationData = integerDataSetter.getValue();
  }

  public TInstruction(final TOp op, final int in, final int out) {
    fOp = op;
    fIn1 = in;
    fOut = out;
  }

  public TInstruction(final TOp op, final int in1, final int in2, final int out) {
    fOp = op;
    fIn1 = in1;
    fIn2 = in2;
    fOut = out;
  }

  public TInstruction(final TOp op, final int out,
      final TActivationDataSetter activationDataSetter) {
    fOp = op;
    fOut = out;
    fActivationData = activationDataSetter.getValue();
  }

  public TInstruction(final TOp op, final int out, final TIntegerDataSetter integerDataSetter) {
    fOp = op;
    fOut = out;
    fActivationData = integerDataSetter.getValue();
  }

  public TInstruction(final TOp op, final int out, final TFloatDataSetter floatDataSetter0,
      TFloatDataSetter floatDataSetter1) {
    fOp = op;
    fOut = out;
    fFloatData0 = floatDataSetter0.getValue();
    fFloatData1 = floatDataSetter1.getValue();
  }

  public TInstruction(final TOp op, final int out, final TFloatDataSetter floatDataSetter0,
      final TFloatDataSetter floatDataSetter1, final TFloatDataSetter floatDataSetter2) {
    fOp = op;
    fOut = out;
    fFloatData0 = floatDataSetter0.getValue();
    fFloatData1 = floatDataSetter1.getValue();
    fFloatData2 = floatDataSetter2.getValue();
  }

  public TInstruction copyFrom(final TInstruction other) {
    fOp = other.fOp;
    fIn1 = other.fIn1;
    fIn2 = other.fIn2;
    fOut = other.fOut;
    fActivationData = other.fActivationData;
    fFloatData0 = other.fFloatData0;
    fFloatData1 = other.fFloatData1;
    fFloatData2 = other.fFloatData2;
    return this;
  }

  public TInstruction(final TInstruction other) {
    this();
    copyFrom(other);
  }

  @Override
  public TInstruction clone() {
    return new TInstruction(this);
  }

  public TOp getOp() {
    return fOp;
  }

  public void setOp(final TOp op) {
    fOp = op;
  }

  public int getIn1() {
    return fIn1;
  }

  public void setIn1(final int in1) {
    fIn1 = in1;
  }

  public int getIn2() {
    return fIn2;
  }

  public void setIn2(final int in2) {
    fIn2 = in2;
  }

  public int getOut() {
    return fOut;
  }

  public void setOut(final int out) {
    fOut = out;
  }

  public double getActivationData() {
    return fActivationData;
  }

  public int getIntegerActivationData() {
    return (int) Math.round(fActivationData);
  }

  public void setActivationData(final double activationData) {
    fActivationData = activationData;
  }

  public void setActivationData(final int integerActivationData) {
    fActivationData = integerActivationData;
  }

  public float getFloatData0() {
    return fFloatData0;
  }

  public void setFloatData0(final float data) {
    fFloatData0 = data;
  }

  public float getFloatData1() {
    return fFloatData1;
  }

  public void setFloatData1(final float data) {
    fFloatData1 = data;
  }

  public float getFloatData2() {
    return fFloatData2;
  }

  public void setFloatData2(final float data) {
    fFloatData2 = data;
  }

  /**
   * 命令にNO_OPをセットして，パラメータをすべて0に初期化する関数
   */
  public void fillWithNoOp() {
    fOp = TOp.NO_OP;
    fIn1 = 0;
    fIn2 = 0;
    fOut = 0;
    fActivationData = 0.0;
    fFloatData0 = 0.0f;
    fFloatData1 = 0.0f;
    fFloatData2 = 0.0f;
  }

  /**
   * 与えられた命令で初期化しつつ入出力をempty(-1)とする
   */
  public void setOpWithEmptyVariables(TOp op) {
    fOp = op;
    fIn1 = -1;
    fIn2 = -1;
    fOut = -1;
    fActivationData = 0.0;
    fFloatData0 = 0.0f;
    fFloatData1 = 0.0f;
    fFloatData2 = 0.0f;
  }

  static public final double kActivationDataTolerance = 0.00001;
  static public final float kFloatDataTolerance = 0.00001f;
  static public final double kVectorDataTolerance = 0.001;
  static public final double kMatrixRowDataTolerance = 0.001;

  /**
   * 与えられた命令とthisが等価であるかの真偽を返却する関数
   */
  @Override
  public boolean equals(final Object other) {
    if (other == this)
      return true;
    if (!(other instanceof TInstruction))
      return false;
    TInstruction otherInstr = (TInstruction) other;
    return fOp == otherInstr.fOp && fIn1 == otherInstr.fIn1 && fIn2 == otherInstr.fIn2
        && fOut == otherInstr.fOut
        && Math.abs(fActivationData - otherInstr.fActivationData) < kActivationDataTolerance
        && Math.abs(fFloatData0 - otherInstr.fFloatData0) < kFloatDataTolerance
        && Math.abs(fFloatData1 - otherInstr.fFloatData1) < kFloatDataTolerance
        && Math.abs(fFloatData2 - otherInstr.fFloatData2) < kFloatDataTolerance;
  }

  @Override
  public String toString() {
    switch (fOp) {
      case NO_OP:
        return "  NoOp()";
      case SCALAR_SUM_OP:
        return "  s" + fOut + " = s" + fIn1 + " + s" + fIn2;
      case SCALAR_DIFF_OP:
        return "  s" + fOut + " = s" + fIn1 + " - s" + fIn2;
      case SCALAR_PRODUCT_OP:
        return "  s" + fOut + " = s" + fIn1 + " * s" + fIn2;
      case SCALAR_DIVISION_OP:
        return "  s" + fOut + " = s" + fIn1 + " / s" + fIn2;
      case SCALAR_MIN_OP:
        return "  s" + fOut + " = minimum(s" + fIn1 + ", s" + fIn2 + ")";
      case SCALAR_MAX_OP:
        return "  s" + fOut + " = maximum(s" + fIn1 + ", s" + fIn2 + ")";
      case SCALAR_ABS_OP:
        return "  s" + fOut + " = abs(s" + fIn1 + ")";
      case SCALAR_HEAVYSIDE_OP:
        return "  s" + fOut + " = heaviside(s" + fIn1 + ", 1.0)";
      case SCALAR_CONST_SET_OP:
        return "  s" + fOut + " = " + fActivationData;
      case SCALAR_SIN_OP:
        return "  s" + fOut + " = sin(s" + fIn1 + ")";
      case SCALAR_COS_OP:
        return "  s" + fOut + " = cos(s" + fIn1 + ")";
      case SCALAR_TAN_OP:
        return "  s" + fOut + " = tan(s" + fIn1 + ")";
      case SCALAR_ARCSIN_OP:
        return "  s" + fOut + " = arcsin(s" + fIn1 + ")";
      case SCALAR_ARCCOS_OP:
        return "  s" + fOut + " = arccos(s" + fIn1 + ")";
      case SCALAR_ARCTAN_OP:
        return "  s" + fOut + " = arctan(s" + fIn1 + ")";
      case SCALAR_EXP_OP:
        return "  s" + fOut + " = exp(s" + fIn1 + ")";
      case SCALAR_LOG_OP:
        return "  s" + fOut + " = log(s" + fIn1 + ")";
      case SCALAR_RECIPROCAL_OP:
        return "  s" + fOut + " = 1 / s" + fIn1;
      case SCALAR_BROADCAST_OP:
        return "  v" + fOut + " = bcast(s" + fIn1 + ")";
      case VECTOR_RECIPROCAL_OP:
        return "  v" + fOut + " = 1 / v" + fIn1;
      case MATRIX_RECIPROCAL_OP:
        return "  m" + fOut + " = 1 / m" + fIn1;
      case MATRIX_ROW_NORM_OP:
        return "  v" + fOut + " = norm(m" + fIn1 + ", axis=1)";
      case MATRIX_COLUMN_NORM_OP:
        return "  v" + fOut + " = norm(m" + fIn1 + ", axis=0)";
      case VECTOR_COLUMN_BROADCAST_OP:
        return "  m" + fOut + " = bcast(v" + fIn1 + ", axis=0)";
      case VECTOR_ROW_BROADCAST_OP:
        return "  m" + fOut + " = bcast(v" + fIn1 + ", axis=1)";
      case VECTOR_SUM_OP:
        return "  v" + fOut + " = v" + fIn1 + " + v" + fIn2;
      case VECTOR_DIFF_OP:
        return "  v" + fOut + " = v" + fIn1 + " - v" + fIn2;
      case VECTOR_PRODUCT_OP:
        return "  v" + fOut + " = v" + fIn1 + " * v" + fIn2;
      case VECTOR_DIVISION_OP:
        return "  v" + fOut + " = v" + fIn1 + " / v" + fIn2;
      case VECTOR_MIN_OP:
        return "  v" + fOut + " = minimum(v" + fIn1 + ", v" + fIn2 + ")";
      case VECTOR_MAX_OP:
        return "  v" + fOut + " = maximum(v" + fIn1 + ", v" + fIn2 + ")";
      case VECTOR_ABS_OP:
        return "  v" + fOut + " = abs(v" + fIn1 + ")";
      case VECTOR_HEAVYSIDE_OP:
        return "  v" + fOut + " = heaviside(v" + fIn1 + ", 1.0)";
      case VECTOR_CONST_SET_OP:
        return "  v" + fOut + "["
            + TInstructionExecutor.floatToIndex(fFloatData0, TInstruction.kFeatureSize) + "]"
            + " = " + fFloatData1;
      case MATRIX_SUM_OP:
        return "  m" + fOut + " = m" + fIn1 + " + m" + fIn2;
      case MATRIX_DIFF_OP:
        return "  m" + fOut + " = m" + fIn1 + " - m" + fIn2;
      case MATRIX_PRODUCT_OP:
        return "  m" + fOut + " = m" + fIn1 + " * m" + fIn2;
      case MATRIX_DIVISION_OP:
        return "  m" + fOut + " = m" + fIn1 + " / m" + fIn2;
      case MATRIX_MIN_OP:
        return "  m" + fOut + " = minimum(m" + fIn1 + ", m" + fIn2 + ")";
      case MATRIX_MAX_OP:
        return "  m" + fOut + " = maximum(m" + fIn1 + ", m" + fIn2 + ")";
      case MATRIX_ABS_OP:
        return "  m" + fOut + " = abs(m" + fIn1 + ")";
      case MATRIX_HEAVYSIDE_OP:
        return "  m" + fOut + " = heaviside(m" + fIn1 + ", 1.0)";
      case MATRIX_CONST_SET_OP:
        return "  m" + fOut + "["
            + TInstructionExecutor.floatToIndex(fFloatData0, TInstruction.kFeatureSize) + ", "
            + TInstructionExecutor.floatToIndex(fFloatData1, TInstruction.kFeatureSize) + "]"
            + " = " + fFloatData2;
      case SCALAR_VECTOR_PRODUCT_OP:
        return "  v" + fOut + " = s" + fIn1 + " * v" + fIn2;
      case VECTOR_INNER_PRODUCT_OP:
        return "  s" + fOut + " = " + "dot(v" + fIn1 + ", v" + fIn2 + ")";
      case VECTOR_OUTER_PRODUCT_OP:
        return "  m" + fOut + " = " + "outer(v" + fIn1 + ", v" + fIn2 + ")";
      case SCALAR_MATRIX_PRODUCT_OP:
        return "  m" + fOut + " = s" + fIn1 + " * m" + fIn2;
      case MATRIX_VECTOR_PRODUCT_OP:
        return "  v" + fOut + " = dot(m" + fIn1 + ", v" + fIn2 + ")";
      case VECTOR_NORM_OP:
        return "  s" + fOut + " = norm(v" + fIn1 + ")";
      case MATRIX_NORM_OP:
        return "  s" + fOut + " = norm(m" + fIn1 + ")";
      case MATRIX_TRANSPOSE_OP:
        return "  m" + fOut + " = transpose(m" + fIn1 + ")";
      case MATRIX_MATRIX_PRODUCT_OP:
        return "  m" + fOut + " = matmul(m" + fIn1 + ", m" + fIn2 + ")";
      case VECTOR_MEAN_OP:
        return "  s" + fOut + " = mean(v" + fIn1 + ")";
      case VECTOR_ST_DEV_OP:
        return "  s" + fOut + " = std(v" + fIn1 + ")";
      case MATRIX_MEAN_OP:
        return "  s" + fOut + " = mean(m" + fIn1 + ")";
      case MATRIX_ST_DEV_OP:
        return "  s" + fOut + " = std(m" + fIn1 + ")";
      case MATRIX_ROW_MEAN_OP:
        return "  v" + fOut + " = mean(m" + fIn1 + ", axis=1)";
      case MATRIX_ROW_ST_DEV_OP:
        return "  v" + fOut + " = std(m" + fIn1 + ", axis=1)";
      case SCALAR_GAUSSIAN_SET_OP:
        return "  s" + fOut + " = gaussian(" + fFloatData0 + ", " + fFloatData1 + ")";
      case VECTOR_GAUSSIAN_SET_OP:
        return "  v" + fOut + " = gaussian(" + fFloatData0 + ", " + fFloatData1 + ", n_features)";
      case MATRIX_GAUSSIAN_SET_OP:
        return "  m" + fOut + " = gaussian(" + fFloatData0 + ", " + fFloatData1
            + ", (n_features, n_features))";
      case SCALAR_UNIFORM_SET_OP:
        return "  s" + fOut + " = uniform(" + fFloatData0 + ", " + fFloatData1 + ")";
      case VECTOR_UNIFORM_SET_OP:
        return "  v" + fOut + " = uniform(" + fFloatData0 + ", " + fFloatData1 + ", n_features)";
      case MATRIX_UNIFORM_SET_OP:
        return "  m" + fOut + " = uniform(" + fFloatData0 + ", " + fFloatData1
            + ", (n_features, n_features))";
      default:
        throw new RuntimeException("invalid op.");
    }
  }

  public TVariable getInput1Variable() {
    TMemoryType memoryType = TOp.getIn1MemoryType(getOp());
    if (memoryType == null)
      return null;
    return new TVariable(getIn1(), memoryType);
  }

  public TVariable getInput2Variable() {
    TMemoryType memoryType = TOp.getIn2MemoryType(getOp());
    if (memoryType == null)
      return null;
    return new TVariable(getIn2(), memoryType);
  }

  public TVariable getOutputVariable() {
    TOp op = getOp();
    TMemoryType memoryType = TOp.getOutMemoryType(op);
    if (memoryType == null) {
      return null;
    }
    if (op == TOp.VECTOR_CONST_SET_OP) {
      TVariable vectorConstSetVariable = new TVariable(getOut(), TMemoryType.VECTOR, true);
      vectorConstSetVariable.addIndex(
          TInstructionExecutor.floatToIndex(getFloatData0(), TInstruction.kFeatureSize), 0);
      return vectorConstSetVariable;
    }
    if (op == TOp.MATRIX_CONST_SET_OP) {
      TVariable matrixConstSetVariable = new TVariable(getOut(), TMemoryType.VECTOR, true);
      matrixConstSetVariable.addIndex(
          TInstructionExecutor.floatToIndex(getFloatData0(), TInstruction.kFeatureSize), 0);
      matrixConstSetVariable.addIndex(
          TInstructionExecutor.floatToIndex(getFloatData1(), TInstruction.kFeatureSize), 0);
      return matrixConstSetVariable;
    }
    return new TVariable(getOut(), memoryType);
  }
}
