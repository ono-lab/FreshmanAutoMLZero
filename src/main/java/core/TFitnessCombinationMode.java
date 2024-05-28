package core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

public enum TFitnessCombinationMode {
  MEAN(), MEDIAN();

  @JsonCreator(mode = Mode.DELEGATING)
  public static TFitnessCombinationMode of(String name) {
    for (var instance : values()) {
      if (instance.name().contentEquals(name))
        return instance;
    }
    throw new IllegalArgumentException();
  }
}
