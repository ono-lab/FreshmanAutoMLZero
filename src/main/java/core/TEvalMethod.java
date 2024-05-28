package core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

public enum TEvalMethod {
  RMS_ERROR(), ACCURACY();

  @JsonCreator(mode = Mode.DELEGATING)
  public static TEvalMethod of(String name) {
    for (var instance : values()) {
      if (instance.name().contentEquals(name))
        return instance;
    }
    throw new IllegalArgumentException();
  }
}
