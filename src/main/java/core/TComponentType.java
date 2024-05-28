package core;

public enum TComponentType {
  SETUP(), PREDICT(), LEARN();

  static public TComponentType[] getAll() {
    return new TComponentType[] {SETUP, PREDICT, LEARN};
  }
}
