package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.TAlgorithm;

/**
 * A Algorithm with no-op instructions.
 */
public class TNoOpModel extends TModel {
  public TNoOpModel() {
    super();
  }

  @JsonCreator
  public TNoOpModel(@JsonProperty("setupInitSize") int setupInitSize,
      @JsonProperty("predictInitSize") int predictInitSize,
      @JsonProperty("learnInitSize") int learnInitSize) {
    super(setupInitSize, predictInitSize, learnInitSize);
  }

  @Override
  protected TAlgorithm getImpl() {
    return new TAlgorithm();
  }
}
