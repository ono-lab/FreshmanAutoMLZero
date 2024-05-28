package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.TAlgorithm;
import core.TAllowedOpsManager;
import core.TComponentRandomizer;
import core.TOp;
import utils.TRandomGenerator;

public class TRandomModel extends TModel {
  private TAllowedOpsManager fAllowedOpsManager;
  private TRandomGenerator fRand;
  private long fSeed;

  @JsonCreator
  public TRandomModel(@JsonProperty("setupInitSize") int setupInitSize,
      @JsonProperty("predictInitSize") int predictInitSize,
      @JsonProperty("learnInitSize") int learnInitSize, @JsonProperty("setupOps") TOp[] setupOps,
      @JsonProperty("predictOps") TOp[] predictOps, @JsonProperty("learnOps") TOp[] learnOps,
      @JsonProperty("seed") long seed) {
    super(setupInitSize, predictInitSize, learnInitSize);
    fAllowedOpsManager = new TAllowedOpsManager(setupOps, predictOps, learnOps);
    fRand = new TRandomGenerator(seed);
    fSeed = seed;
  }

  @Override
  protected TAlgorithm getImpl() {
    TAlgorithm algorithm = new TAlgorithm();
    fillComponentWithInstruction(algorithm.getSetup(), getSetupInitSize(), kNoOpInstruction);
    fillComponentWithInstruction(algorithm.getPredict(), getPredictInitSize(), kNoOpInstruction);
    fillComponentWithInstruction(algorithm.getLearn(), getLearnInitSize(), kNoOpInstruction);
    TComponentRandomizer.executeAll(algorithm, fAllowedOpsManager, fRand);
    return algorithm;
  }

  @Override
  public void initialize(int seed) {
    fRand = new TRandomGenerator(fSeed + seed);
  }
}
