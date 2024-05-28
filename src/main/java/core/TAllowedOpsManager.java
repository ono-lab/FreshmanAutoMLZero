package core;

import java.util.ArrayList;
import utils.TRandomGenerator;
import utils.TArrayUtility;

/**
 * 各コンポーネント(setup, predict, learn)に許可する命令を管理する用のクラス
 */
public class TAllowedOpsManager {
  /**
   * Ops that can be introduced into the setup component function. Empty means the component
   * function is not randomized.
   */
  private TOp[] fAllowedSetupOps;
  private ArrayList<TOp> fAllowedSetupOpsWithScalarOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedSetupOpsWithVectorOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedSetupOpsWithMatrixOutput = new ArrayList<TOp>();

  /**
   * Ops that can be introduced into the predict component function. // Empty means the component
   * function is not randomized.
   */
  private TOp[] fAllowedPredictOps;

  private ArrayList<TOp> fAllowedPredictOpsWithScalarOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedPredictOpsWithVectorOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedPredictOpsWithMatrixOutput = new ArrayList<TOp>();


  private ArrayList<TOp> fAllowedPredictOpsWithScalarInput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedPredictOpsWithVectorInput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedPredictOpsWithMatrixInput = new ArrayList<TOp>();

  /**
   * Ops that can be introduced into the learn component function. // Empty means the component
   * function is not randomized.
   */
  private TOp[] fAllowedLearnOps;;
  private ArrayList<TOp> fAllowedLearnOpsWithScalarOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedLearnOpsWithVectorOutput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedLearnOpsWithMatrixOutput = new ArrayList<TOp>();

  private ArrayList<TOp> fAllowedLearnOpsWithScalarInput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedLearnOpsWithVectorInput = new ArrayList<TOp>();
  private ArrayList<TOp> fAllowedLearnOpsWithMatrixInput = new ArrayList<TOp>();

  public TAllowedOpsManager(TOp[] allowedSetupOps, TOp[] allowedPredictOps, TOp[] allowedLearnOps) {
    // Setup
    fAllowedSetupOps = allowedSetupOps;
    for (TOp op : fAllowedSetupOps) {
      TMemoryType outMemoryType = TOp.getOutMemoryType(op);
      if (outMemoryType != null) {
        switch (outMemoryType) {
          case SCALAR:
            fAllowedSetupOpsWithScalarOutput.add(op);
            break;
          case VECTOR:
            fAllowedSetupOpsWithVectorOutput.add(op);
            break;
          case MATRIX:
            fAllowedSetupOpsWithMatrixOutput.add(op);
            break;
        }
      }
    }

    // Predict
    fAllowedPredictOps = allowedPredictOps;
    // outputの型を指定して取り出せるように分類しておく
    for (TOp op : fAllowedPredictOps) {
      TMemoryType outMemoryType = TOp.getOutMemoryType(op);
      if (outMemoryType != null) {
        switch (outMemoryType) {
          case SCALAR:
            fAllowedPredictOpsWithScalarOutput.add(op);
            break;
          case VECTOR:
            fAllowedPredictOpsWithVectorOutput.add(op);
            break;
          case MATRIX:
            fAllowedPredictOpsWithMatrixOutput.add(op);
            break;
        }
      }

      TMemoryType in1MemoryType = TOp.getIn1MemoryType(op);
      if (in1MemoryType != null) {
        switch (in1MemoryType) {
          case SCALAR:
            fAllowedPredictOpsWithScalarInput.add(op);
            break;
          case VECTOR:
            fAllowedPredictOpsWithVectorInput.add(op);
            break;
          case MATRIX:
            fAllowedPredictOpsWithMatrixInput.add(op);
            break;
        }
      }
      TMemoryType in2MemoryType = TOp.getIn2MemoryType(op);
      if (in2MemoryType == null || in1MemoryType == in2MemoryType) {
        continue;
      }
      if (in2MemoryType != null) {
        switch (in2MemoryType) {
          case SCALAR:
            fAllowedPredictOpsWithScalarInput.add(op);
            break;
          case VECTOR:
            fAllowedPredictOpsWithVectorInput.add(op);
            break;
          case MATRIX:
            fAllowedPredictOpsWithMatrixInput.add(op);
            break;
        }
      }
    }

    // Learn
    fAllowedLearnOps = allowedLearnOps;
    // outputの型を指定して取り出せるように分類しておく
    for (TOp op : fAllowedLearnOps) {
      TMemoryType memoryType = TOp.getOutMemoryType(op);
      if (memoryType != null) {
        switch (memoryType) {
          case SCALAR:
            fAllowedLearnOpsWithScalarOutput.add(op);
            break;
          case VECTOR:
            fAllowedLearnOpsWithVectorOutput.add(op);
            break;

          case MATRIX:
            fAllowedLearnOpsWithMatrixOutput.add(op);
            break;
        }
      }

      TMemoryType in1MemoryType = TOp.getIn1MemoryType(op);
      if (in1MemoryType != null) {
        switch (in1MemoryType) {
          case SCALAR:
            fAllowedLearnOpsWithScalarInput.add(op);
            break;
          case VECTOR:
            fAllowedLearnOpsWithVectorInput.add(op);
            break;
          case MATRIX:
            fAllowedLearnOpsWithMatrixInput.add(op);
            break;
        }
      }
      TMemoryType in2MemoryType = TOp.getIn2MemoryType(op);
      if (in2MemoryType == null || in1MemoryType == in2MemoryType) {
        continue;
      }
      if (in2MemoryType != null) {
        switch (in2MemoryType) {
          case SCALAR:
            fAllowedLearnOpsWithScalarInput.add(op);
            break;
          case VECTOR:
            fAllowedLearnOpsWithVectorInput.add(op);
            break;
          case MATRIX:
            fAllowedLearnOpsWithMatrixInput.add(op);
            break;
        }
      }
    }
  }

  public TOp[] getAllowedSetupOps() {
    return fAllowedSetupOps;
  }

  public boolean isAllowedSetupOpsPresent() {
    return fAllowedSetupOps.length != 0;
  }

  public TOp[] getAllowedPredictOps() {
    return fAllowedPredictOps;
  }

  public boolean isAllowedPredictOpsPresent() {
    return fAllowedPredictOps.length != 0;
  }

  public TOp[] getAllowedLearnOps() {
    return fAllowedLearnOps;
  }

  public boolean isAllowedLearnOpsPresent() {
    return fAllowedLearnOps.length != 0;
  }

  public boolean isAllowedOpsPresent(TComponentType type) {
    switch (type) {
      case SETUP:
        return isAllowedSetupOpsPresent();
      case PREDICT:
        return isAllowedPredictOpsPresent();
      case LEARN:
        return isAllowedLearnOpsPresent();
      default:
        throw new RuntimeException("Invalid component type.");
    }
  }

  private void addOpIfMatchInputMemoryTypes(ArrayList<TOp> ops, TOp op, TMemoryType inMemoryType1,
      TMemoryType inMemoryType2) {
    TMemoryType in1MemoryType = TOp.getIn1MemoryType(op);
    TMemoryType in2MemoryType = TOp.getIn2MemoryType(op);
    if (inMemoryType1 == null && inMemoryType2 == null
        || inMemoryType1 == null && inMemoryType2 == in1MemoryType
        || inMemoryType1 == null && inMemoryType2 == in2MemoryType
        || inMemoryType2 == null && inMemoryType1 == in1MemoryType
        || inMemoryType2 == null && inMemoryType1 == in2MemoryType
        || inMemoryType1 == in1MemoryType && inMemoryType2 == in2MemoryType
        || inMemoryType1 == in2MemoryType && inMemoryType2 == in1MemoryType) {
      ops.add(op);
    }
  }

  private void addOpIfStrictMatchInputMemoryTypes(ArrayList<TOp> ops, TOp op, TMemoryType inMemoryType1,
      TMemoryType inMemoryType2) {
    TMemoryType in1MemoryType = TOp.getIn1MemoryType(op);
    TMemoryType in2MemoryType = TOp.getIn2MemoryType(op);
    if (inMemoryType1 == in1MemoryType && inMemoryType2 == in2MemoryType
        || inMemoryType1 == in2MemoryType && inMemoryType2 == in1MemoryType) {
      ops.add(op);
    }
  }

  /**
   * setup用の命令群の中からランダムに1つ選択して返却する関数
   */
  public TOp getRandomSetupOp(TRandomGenerator rand) {
    int opIndex = rand.nextInt(0, fAllowedSetupOps.length);
    return fAllowedSetupOps[opIndex];
  }

  /**
   * setup用の命令群の中から入力と出力が条件に合う命令をランダムに1つ選択して返却する関数。
   * outMemoryTypesは複数指定した場合にORになる。
   */
  public TOp getMemoryTypeDesignatedRandomSetupOp(ArrayList<TMemoryType> outMemoryTypes,
      TRandomGenerator rand) {
    ArrayList<TOp> ops = new ArrayList<TOp>();
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.SCALAR)) {
      ops.addAll(fAllowedSetupOpsWithScalarOutput);
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.VECTOR)) {
      ops.addAll(fAllowedSetupOpsWithVectorOutput);
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.MATRIX)) {
      ops.addAll(fAllowedSetupOpsWithMatrixOutput);
    }
    if (ops.isEmpty())
      return null;
    int opIndex = rand.nextInt(0, ops.size());
    return ops.get(opIndex);
  }

  /**
   * predict用の命令群の中からランダムに1つ選択して返却する関数
   */
  public TOp getRandomPredictOp(TRandomGenerator rand) {
    int opIndex = rand.nextInt(0, fAllowedPredictOps.length);
    return fAllowedPredictOps[opIndex];
  }

  /**
   * predict用の命令群の中から入力と出力が条件に合う命令をランダムに1つ選択して返却する関数。
   * outMemoryTypesは複数指定した場合にORになる。inMemoryTypeは指定しない場合はnullを入れる。 inMemoryType1とinMemoryType2は順不同。
   */
  public TOp getMemoryTypeDesignatedRandomPredictOp(ArrayList<TMemoryType> outMemoryTypes,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    ArrayList<TOp> ops = new ArrayList<TOp>();
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.SCALAR)) {
      for (TOp op : fAllowedPredictOpsWithScalarOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.VECTOR)) {
      for (TOp op : fAllowedPredictOpsWithVectorOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.MATRIX)) {
      for (TOp op : fAllowedPredictOpsWithMatrixOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (except != null) {
      ops.remove(except);
    }
    if (ops.isEmpty())
      return null;
    int opIndex = rand.nextInt(0, ops.size());
    return ops.get(opIndex);
  }

    /**
   * predict用の命令群の中から入力と出力が条件に合う命令をランダムに1つ選択して返却する関数。
   * outMemoryTypesは複数指定した場合にORになる。inMemoryTypeは指定しない場合はnullを入れる。 inMemoryType1とinMemoryType2は順不同。
   */
  public TOp getMemoryTypeDesignatedRandomPredictOp(TMemoryType outMemoryType,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    ArrayList<TMemoryType> outMemoryTypes = new ArrayList<TMemoryType>();
    outMemoryTypes.add(outMemoryType);
    return getMemoryTypeDesignatedRandomPredictOp(outMemoryTypes, inMemoryType1, inMemoryType2,  rand, except);
  }

  /**
   * predict用の命令群の中から入力と出力が条件に合う命令をランダムに1つ選択して返却する関数。
   * こちらはnullであることも完全一致しなければならない
   */
  public TOp getStrictMemoryTypeDesignatedRandomPredictOp(TMemoryType outMemoryType,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    assert outMemoryType != null;
    ArrayList<TOp> ops = new ArrayList<TOp>();
    switch (outMemoryType) {
      case SCALAR:
        for (TOp op : fAllowedPredictOpsWithScalarOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;
      case VECTOR:
        for (TOp op : fAllowedPredictOpsWithVectorOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;

      case MATRIX:
        for (TOp op : fAllowedPredictOpsWithMatrixOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;
    }
    if (except != null) {
      ops.remove(except);
    }
    if (ops.isEmpty())
      return null;
    int opIndex = rand.nextInt(0, ops.size());
    return ops.get(opIndex);
  }

  /**
   * learn用の命令群の中からランダムに1つ選択して返却する関数
   */
  public TOp getRandomLearnOp(TRandomGenerator rand) {
    int opIndex = rand.nextInt(0, fAllowedLearnOps.length);
    return fAllowedLearnOps[opIndex];
  }

  /**
   * learn用の命令群の中から出力先の型がmemoryTypesのいずれかに合うランダムに1つ選択して返却する関数
   * outMemoryTypesは複数指定した場合にORになる。inMemoryTypeは指定しない場合はnullを入れる。 inMemoryType1とinMemoryType2は順不同。
   */
  public TOp getMemoryTypeDesignatedRandomLearnOp(ArrayList<TMemoryType> outMemoryTypes,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    ArrayList<TOp> ops = new ArrayList<TOp>();
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.SCALAR)) {
      for (TOp op : fAllowedLearnOpsWithScalarOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.VECTOR)) {
      for (TOp op : fAllowedLearnOpsWithVectorOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (TArrayUtility.include(outMemoryTypes, TMemoryType.MATRIX)) {
      for (TOp op : fAllowedLearnOpsWithMatrixOutput) {
        addOpIfMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
      }
    }
    if (except != null) {
      ops.remove(except);
    }
    if (ops.size() == 0) {
      return null;
    }
    int opIndex = rand.nextInt(0, ops.size());
    return ops.get(opIndex);
  }


  /**
   * learn用の命令群の中から出力先の型がmemoryTypesのいずれかに合うランダムに1つ選択して返却する関数
   * outMemoryTypesは複数指定した場合にORになる。inMemoryTypeは指定しない場合はnullを入れる。 inMemoryType1とinMemoryType2は順不同。
   */
  public TOp getMemoryTypeDesignatedRandomLearnOp(TMemoryType outMemoryType,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    ArrayList<TMemoryType> outMemoryTypes = new ArrayList<TMemoryType>();
    outMemoryTypes.add(outMemoryType);
    return getMemoryTypeDesignatedRandomLearnOp(outMemoryTypes,
        inMemoryType1, inMemoryType2, rand, except);
  }

  /**
   * learn用の命令群の中から出力先の型がmemoryTypesのいずれかに合うランダムに1つ選択して返却する関数
   * こちらはnullであることも完全一致しなければならない
   */
  public TOp getStrictMemoryTypeDesignatedRandomLearnOp(TMemoryType outMemoryType,
      TMemoryType inMemoryType1, TMemoryType inMemoryType2, TRandomGenerator rand, TOp except) {
    assert outMemoryType != null;
    ArrayList<TOp> ops = new ArrayList<TOp>();
    switch (outMemoryType) {
      case SCALAR:
        for (TOp op : fAllowedLearnOpsWithScalarOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;
      case VECTOR:
        for (TOp op : fAllowedLearnOpsWithVectorOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;

      case MATRIX:
        for (TOp op : fAllowedLearnOpsWithMatrixOutput) {
          addOpIfStrictMatchInputMemoryTypes(ops, op, inMemoryType1, inMemoryType2);
        }
        break;
    }
    if (except != null) {
      ops.remove(except);
    }
    if (ops.isEmpty())
      return null;
    int opIndex = rand.nextInt(0, ops.size());
    return ops.get(opIndex);
  }

  /**
   * 指定されたコンポーネントに対して許可される命令群の中からランダムに1つ選択して返却する関数
   */
  public TOp getRandomOp(TComponentType type, TRandomGenerator rand) {
    assert isAllowedOpsPresent(type);
    switch (type) {
      case SETUP:
        return getRandomSetupOp(rand);
      case PREDICT:
        return getRandomPredictOp(rand);
      case LEARN:
        return getRandomLearnOp(rand);
      default:
        throw new RuntimeException("Invalid component type.");
    }
  }

  public TOp getOutMemoryTypeDesignatedRandomOp(TMemoryType outMemoryType, TComponentType type, TRandomGenerator rand) {
    assert isAllowedOpsPresent(type);
    ArrayList<TOp> candidates = null;
    switch (type) {
      case SETUP: {
        switch (outMemoryType) {
          case SCALAR:
            candidates = fAllowedSetupOpsWithScalarOutput;
            break;

          case VECTOR:
            candidates = fAllowedSetupOpsWithVectorOutput;
            break;

          case MATRIX:
            candidates = fAllowedSetupOpsWithMatrixOutput;
            break;
        }
        break;
      }
      case PREDICT: {
        switch (outMemoryType) {
          case SCALAR:
            candidates = fAllowedPredictOpsWithScalarOutput;
            break;

          case VECTOR:
            candidates = fAllowedPredictOpsWithVectorOutput;
            break;

          case MATRIX:
            candidates = fAllowedPredictOpsWithMatrixOutput;
            break;
        }
        break;
      }
      case LEARN: {
        switch (outMemoryType) {
          case SCALAR:
            candidates = fAllowedLearnOpsWithScalarOutput;
            break;

          case VECTOR:
            candidates = fAllowedLearnOpsWithVectorOutput;
            break;

          case MATRIX:
            candidates = fAllowedLearnOpsWithMatrixOutput;
            break;
        }
        break;
      }
    }
    if (candidates == null || candidates.size() == 0)
      return null;
    int candidateIndex = rand.nextInt(candidates.size());
    return candidates.get(candidateIndex);
  }
}
