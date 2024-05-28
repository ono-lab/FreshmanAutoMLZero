package core.instruction_data_setter;

public class TDataSetter<T> {
  protected T fValue;

  public TDataSetter(T value) {
    fValue = value;
  }

  public T getValue() {
    return fValue;
  }
}
