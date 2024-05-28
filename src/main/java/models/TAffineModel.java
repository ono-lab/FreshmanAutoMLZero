package models;

import java.util.ArrayList;
import core.TAlgorithm;
import core.TInstruction;
import core.TMemory;
import core.TOp;
import core.instruction_data_setter.TActivationDataSetter;

/**
 * A linear model with learning by gradient descent.
 */
public class TAffineModel extends TModel {
  public static final int kLinearAlgorithmWeightsAddress = 1;
  public static final int kLinearAlgorithmInterceptAddress = 4;
  private static final double kDefaultLearningRate = 0.01;

  private double fLearningRate = kDefaultLearningRate;

  // Scalar addresses
  // 0 : label
  // 1 : prediction
  private static final int kLearningRateAddress = 2;
  private static final int kPredictionErrorAddress = 3;
  private static final int kInterceptAddress = 4;
  private static final int kTmpPredictionAddress = 3;

  // Vector addresses.
  // 0 : feature
  private static final int kWeightsAddress = 1;
  private static final int kCorrectionAddress = 2;

  private void verifyAddresses() {
    assert TMemory.numOfScalarAddresses >= 4;
    assert TMemory.numOfVectorAddresses >= 3;
    assert TMemory.numOfMatrixAddresses >= 0;
  }

  public TAffineModel() {
    super();
    verifyAddresses();
  }

  public TAffineModel(double learningRate) {
    this();
    fLearningRate = learningRate;
  }

  public TAffineModel(int setupInitSize, int predictInitSize, int learnInitSize) {
    super(setupInitSize, predictInitSize, learnInitSize);
    verifyAddresses();
  }

  public TAffineModel(int setupInitSize, int predictInitSize, int learnInitSize,
      double learningRate) {
    this(setupInitSize, predictInitSize, learnInitSize);
    fLearningRate = learningRate;
  }

  @Override
  protected TAlgorithm getImpl() {
    TAlgorithm algorithm = new TAlgorithm();

    // setup
    ArrayList<TInstruction> setup = algorithm.getSetup();
    setup.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, kLearningRateAddress,
        new TActivationDataSetter(fLearningRate)));

    // predict
    ArrayList<TInstruction> predict = algorithm.getPredict();
    predict.add(new TInstruction(TOp.VECTOR_INNER_PRODUCT_OP, kWeightsAddress,
        TMemory.kFeatureVectorAddress, kTmpPredictionAddress));
    predict.add(new TInstruction(TOp.SCALAR_SUM_OP, kTmpPredictionAddress,
        kInterceptAddress, TMemory.kPredictionScalarAddress));

    // learn
    ArrayList<TInstruction> learn = algorithm.getLearn();
    learn.add(new TInstruction(TOp.SCALAR_DIFF_OP, TMemory.kLabelScalarAddress,
        TMemory.kPredictionScalarAddress, kPredictionErrorAddress));
    learn.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, kLearningRateAddress, kPredictionErrorAddress,
        kPredictionErrorAddress));
    learn.add(new TInstruction(TOp.SCALAR_VECTOR_PRODUCT_OP, kPredictionErrorAddress,
        TMemory.kFeatureVectorAddress, kCorrectionAddress));
    learn.add(
        new TInstruction(TOp.VECTOR_SUM_OP, kWeightsAddress, kCorrectionAddress, kWeightsAddress));
    return algorithm;
  }
}
