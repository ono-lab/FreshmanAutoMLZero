package core.algorithm_tree;
import core.TMemoryType;

abstract public class TNode {
  private TMemoryType fType;

  TNode(TMemoryType type) {
    fType = type;
  }

  public TMemoryType getMemoryType(){
    return fType;
  }
}
