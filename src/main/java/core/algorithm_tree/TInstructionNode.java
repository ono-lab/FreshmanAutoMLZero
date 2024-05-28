package core.algorithm_tree;
import core.*;
import utils.TRandomGenerator;

public class TInstructionNode extends TNode {
  // 命令の種類
  private TOp fOp = TOp.NO_OP;

  private TNode fIn1;
  private TNode fIn2;

  // 以下はメモリ以外からメモリにデータを渡す際に使用する変数である．
  // 例えば，定数代入の定数部分，定数代入の場所を表すindex，正規乱数の平均や分散等
  // fActivationDataはスカラーの定数代入のみで使用していて，
  // 学習率等で精度が重要となる場合が想定されるためdouble型となっている．
  // private double fActivationData = 0.0;
  // private float fFloatData0 = 0.0f;
  // private float fFloatData1 = 0.0f;
  // private float fFloatData2 = 0.0f;

  public TInstructionNode(TMemoryType memoryType) {
    super(memoryType);
  }

  public TInstructionNode(TMemoryType memoryType, TOp op) {
    super(memoryType);
    if (memoryType != TOp.getOutMemoryType(op)) {
      throw new Error("memory type is not match op output.");
    }
    fOp = op;
  }

  public int getInputNum() {
    return TOp.getInNum(fOp);
  }

  public TMemoryType getIn1MemoryType() {
    return TOp.getIn1MemoryType(fOp);
  }

  public TMemoryType getIn2MemoryType() {
    return TOp.getIn2MemoryType(fOp);
  }

  public void setIn1Node(TNode node) {
    fIn1 = node;
  }

  public void setIn2Node(TNode node) {
    fIn2 = node;
  }

  public boolean isAvailableIn1() {
    return fIn1 == null;
  }
  
  public boolean isAvailableIn2() {
    return TOp.getInNum(fOp) == 2 && fIn2 == null;
  }

  public Integer getAvailableInput(TRandomGenerator rand){
    if(isAvailableIn1() && isAvailableIn2()){
      return rand.nextChoice2();
    } else if (isAvailableIn1()) {
      return 0;
    } else if (isAvailableIn2()) {
      return 1;
    } else {
      return null;
    }
  }
  
  public boolean isFilled() {
    return !isAvailableIn1() && !isAvailableIn2(); 
  }
}
