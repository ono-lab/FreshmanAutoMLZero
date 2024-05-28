package core;

public class TVariable {
  private int fAddress;
  private TMemoryType fMemoryType;
  private boolean fLimited = false;
  private boolean[][] fFill = null;

  public TVariable(int address, TMemoryType memoryType) {
    fAddress = address;
    fMemoryType = memoryType;
  }

  public TVariable(TVariable other) {
    fAddress = other.fAddress;
    fMemoryType = other.fMemoryType;
    fLimited = other.fLimited;
    fFill = other.fFill;
  }

  public TVariable(int address, TMemoryType memoryType, boolean limited) {
    this(address, memoryType);
    fLimited = true;
    fFill = new boolean[TInstruction.kFeatureSize][TInstruction.kFeatureSize];
    for (int row = 0; row < TInstruction.kFeatureSize; row++) {
      for (int col = 0; col < TInstruction.kFeatureSize; col++) {
        fFill[row][col] = false;
      }
    }
  }

  public TVariable clone() {
    return new TVariable(this);
  }

  public void addIndex(int row, int col) {
    if (!fLimited)
      throw new RuntimeException("This is not limited variable.");
    fFill[row][col] = true;
  }

  boolean canOverwrite(TVariable other) {
    if (fAddress != other.fAddress || fMemoryType != other.fMemoryType) {
      return false;
    }
    if (fLimited) {
      for (int row = 0; row < TInstruction.kFeatureSize; row++) {
        for (int col = 0; col < TInstruction.kFeatureSize; col++) {
          if (!fFill[row][col] && !other.fLimited || !fFill[row][col] && other.fFill[row][col]) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public TMemoryType getMemoryType() {
    return fMemoryType;
  }

  public int getAddress() {
    return fAddress;
  }

  public boolean isBlank() {
    return fAddress < 0;
  }

  public void marge(TVariable other) {
    if (!equals(other))
      throw new RuntimeException("This is not equal variable.");
    if (!fLimited || !other.fLimited) {
      fLimited = false;
      fFill = null;
      return;
    }
    for (int row = 0; row < TInstruction.kFeatureSize; row++) {
      for (int col = 0; col < TInstruction.kFeatureSize; col++) {
        fFill[row][col] = fFill[row][col] || other.fFill[row][col];
      }
    }
  }

  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (!(other instanceof TVariable))
      return false;
    TVariable otherVariable = (TVariable) other;
    if (fMemoryType != otherVariable.fMemoryType || fAddress != otherVariable.fAddress)
      return false;
    return true;
  }

  @Override
  public String toString() {
    switch (fMemoryType) {
      case SCALAR:
        return "s" + fAddress;
      case VECTOR:
        return "v" + fAddress;
      case MATRIX:
        return "m" + fAddress;
    }
    return "invalid";
  }
}
