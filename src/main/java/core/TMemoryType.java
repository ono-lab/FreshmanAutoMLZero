package core;

import java.util.ArrayList;

public enum TMemoryType {
  MATRIX(), VECTOR(), SCALAR();

  public static ArrayList<TMemoryType> getAll(){
    ArrayList<TMemoryType> allMemoryTypes = new ArrayList<TMemoryType>();
    allMemoryTypes.add(TMemoryType.SCALAR);
    allMemoryTypes.add(TMemoryType.VECTOR);
    allMemoryTypes.add(TMemoryType.MATRIX);
    return allMemoryTypes;
  }
}
