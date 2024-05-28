package models;

import java.util.ArrayList;
import core.TAlgorithm;
import core.TInstruction;
import core.TMemory;
import core.TOp;
import core.instruction_data_setter.TActivationDataSetter;

public class TNeuralNetNoBiasNoGradientModel extends TModel {
  private static final double kDefaultLearningRate = 0.01;

  private double fLearningRate = kDefaultLearningRate;

  // A 2-layer neural network without bias and no learning.
  public static final int kUnitTestNeuralNetNoBiasNoGradientFinalLayerWeightsAddress = 1;
  public static final int kUnitTestNeuralNetNoBiasNoGradientFirstLayerWeightsAddress = 0;

  // Scalar addresses
  // 0 : label
  // 1 : prediction
  private static final int kLearningRateAddress = 2;
  private static final int kPredictionErrorAddress = 3;

  // Vector addresses.
  // 0 : feature
  private static final int kFinalLayerWeightsAddress = kUnitTestNeuralNetNoBiasNoGradientFinalLayerWeightsAddress;
  private static final int kFirstLayerOutputBeforeReluAddress = 2;
  private static final int kFirstLayerOutputAfterReluAddress = 3;
  private static final int kZerosAddress = 4;
  private static final int kGradientWrtFinalLayerWeightsAddress = 5;
  private static final int kGradientWrtActivationsAddress = 6;
  private static final int kGradientOfReluAddress = 7;

  // Matrix addresses.
  private static final int kFirstLayerWeightsAddress = kUnitTestNeuralNetNoBiasNoGradientFirstLayerWeightsAddress;
  private static final int kGradientWrtFirstLayerWeightsAddress = 1;

  private void verifyAddresses() {
    assert TMemory.numOfScalarAddresses >= 4;
    assert TMemory.numOfVectorAddresses >= 8;
    assert TMemory.numOfMatrixAddresses >= 8;
  }

  public TNeuralNetNoBiasNoGradientModel() {
    super();
    verifyAddresses();
  }

  public TNeuralNetNoBiasNoGradientModel(double learningRate) {
    this();
    fLearningRate = learningRate;
  }

  public TNeuralNetNoBiasNoGradientModel(int setupInitSize, int predictInitSize,
      int learnInitSize) {
    super(setupInitSize, predictInitSize, learnInitSize);
    verifyAddresses();
  }

  public TNeuralNetNoBiasNoGradientModel(int setupInitSize, int predictInitSize, int learnInitSize,
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
    // Multiply with first layer weight matrix.
    predict.add(new TInstruction(TOp.MATRIX_VECTOR_PRODUCT_OP, kFirstLayerWeightsAddress,
        TMemory.kFeatureVectorAddress, kFirstLayerOutputBeforeReluAddress));
    // Apply RELU.
    predict.add(new TInstruction(TOp.VECTOR_MAX_OP, kFirstLayerOutputBeforeReluAddress,
        kZerosAddress, kFirstLayerOutputAfterReluAddress));
    // Dot product with final layer weight vector.
    predict.add(new TInstruction(TOp.VECTOR_INNER_PRODUCT_OP, kFirstLayerOutputAfterReluAddress,
        kFinalLayerWeightsAddress, TMemory.kPredictionScalarAddress));

    // learn
    ArrayList<TInstruction> learn = algorithm.getLearn();
    learn.add(new TInstruction(TOp.SCALAR_DIFF_OP, TMemory.kLabelScalarAddress,
        TMemory.kPredictionScalarAddress, kPredictionErrorAddress));
    learn.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, kLearningRateAddress, kPredictionErrorAddress,
        kPredictionErrorAddress));
    learn.add(new TInstruction(TOp.SCALAR_VECTOR_PRODUCT_OP, kPredictionErrorAddress,
        kFirstLayerOutputAfterReluAddress, kGradientWrtFinalLayerWeightsAddress));
    learn.add(new TInstruction(TOp.VECTOR_SUM_OP, kFinalLayerWeightsAddress,
        kGradientWrtFinalLayerWeightsAddress, kFinalLayerWeightsAddress));
    learn.add(new TInstruction(TOp.SCALAR_VECTOR_PRODUCT_OP, kPredictionErrorAddress,
        kFinalLayerWeightsAddress, kGradientWrtActivationsAddress));
    learn.add(new TInstruction(TOp.VECTOR_HEAVYSIDE_OP, kFirstLayerOutputBeforeReluAddress, 0,
        kGradientOfReluAddress));
    learn.add(new TInstruction(TOp.VECTOR_PRODUCT_OP, kGradientOfReluAddress,
        kGradientWrtActivationsAddress, kGradientWrtActivationsAddress));
    learn.add(new TInstruction(TOp.VECTOR_OUTER_PRODUCT_OP, kGradientWrtActivationsAddress,
        TMemory.kFeatureVectorAddress, kGradientWrtFirstLayerWeightsAddress));
    learn.add(new TInstruction(TOp.MATRIX_SUM_OP, kFirstLayerWeightsAddress,
        kGradientWrtFirstLayerWeightsAddress, kFirstLayerWeightsAddress));
    return algorithm;
  }

  public static void main(String[] args) {
    TNeuralNetNoBiasNoGradientModel model = new TNeuralNetNoBiasNoGradientModel();
    TAlgorithm algorithm = model.get();
    System.out.println(algorithm);
  }
}
