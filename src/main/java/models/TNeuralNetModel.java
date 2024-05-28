package models;

import java.util.ArrayList;
import core.TAlgorithm;
import core.TInstruction;
import core.TMemory;
import core.TOp;
import core.instruction_data_setter.TActivationDataSetter;
import core.instruction_data_setter.TFloatDataSetter;

/**
 * A 2-layer neural network with one nonlinearity, where both layers implement learning by gradient
 * descent. The weights are initialized randomly.
 */
public class TNeuralNetModel extends TModel {
  private static final double kDefaultLearningRate = 0.01;
  private static final float kDefaultInitScale = 0.1f;

  private double fLearningRate = kDefaultLearningRate;
  private float fFirstInitScale = kDefaultInitScale;
  private float fFinalInitScale = kDefaultInitScale;

  // Scalar addresses
  // 0 : label
  // 1 : prediction
  private static final int kFinalLayerBiasAddress = 2;
  private static final int kLearningRateAddress = 3;
  private static final int kPredictionErrorAddress = 4;

  // Vector addresses.
  // 0 : feature
  private static final int kFirstLayerBiasAddress = 1;
  private static final int kFinalLayerWeightsAddress = 2;
  private static final int kFirstLayerOutputBeforeReluAddress = 3;
  private static final int kFirstLayerOutputAfterReluAddress = 4;
  private static final int kZerosAddress = 5;
  private static final int kGradientWrtFinalLayerWeightsAddress = 6;
  private static final int kGradientWrtActivationsAddress = 7;
  private static final int kGradientOfReluAddress = 8;

  // Matrix addresses.
  private static final int kFirstLayerWeightsAddress = 0;
  private static final int kGradientWrtFirstLayerWeightsAddress = 1;

  private void verifyAddresses() {
    assert TMemory.numOfScalarAddresses >= 2;
    assert TMemory.numOfScalarAddresses >= 9;
    assert TMemory.numOfScalarAddresses >= 5;
  }

  private void setHyperparameter(double learningRate, float firstInitScale, float finalInitScale) {
    fLearningRate = learningRate;
    fFirstInitScale = finalInitScale;
    fFinalInitScale = finalInitScale;
  }


  public TNeuralNetModel() {
    super();
    verifyAddresses();
  }

  public TNeuralNetModel(double learningRate, float firstInitScale, float finalInitScale) {
    this();
    setHyperparameter(learningRate, firstInitScale, finalInitScale);
  }

  public TNeuralNetModel(int setupInitSize, int predictInitSize, int learnInitSize) {
    super(setupInitSize, predictInitSize, learnInitSize);
    verifyAddresses();
  }

  public TNeuralNetModel(int setupInitSize, int predictInitSize, int learnInitSize,
      double learningRate, float firstInitScale, float finalInitScale) {
    this(setupInitSize, predictInitSize, learnInitSize);
    setHyperparameter(learningRate, firstInitScale, finalInitScale);
  }

  @Override
  protected TAlgorithm getImpl() {
    TAlgorithm algorithm = new TAlgorithm();

    // setup
    ArrayList<TInstruction> setup = algorithm.getSetup();
    setup.add(new TInstruction(TOp.VECTOR_GAUSSIAN_SET_OP, kFinalLayerWeightsAddress,
        new TFloatDataSetter(0.0f), new TFloatDataSetter(fFinalInitScale)));
    setup.add(new TInstruction(TOp.MATRIX_GAUSSIAN_SET_OP, kFirstLayerWeightsAddress,
        new TFloatDataSetter(0.0f), new TFloatDataSetter(fFirstInitScale)));
    setup.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, kLearningRateAddress,
        new TActivationDataSetter(fLearningRate)));

    // predict
    ArrayList<TInstruction> predict = algorithm.getPredict();
    // Multiply with first layer weight matrix.
    predict.add(new TInstruction(TOp.MATRIX_VECTOR_PRODUCT_OP, kFirstLayerWeightsAddress,
        TMemory.kFeatureVectorAddress, kFirstLayerOutputBeforeReluAddress));
    // Add first layer bias.
    predict.add(new TInstruction(TOp.VECTOR_SUM_OP, kFirstLayerOutputBeforeReluAddress,
        kFirstLayerBiasAddress, kFirstLayerOutputBeforeReluAddress));
    // Apply RELU.
    predict.add(new TInstruction(TOp.VECTOR_MAX_OP, kFirstLayerOutputBeforeReluAddress,
        kZerosAddress, kFirstLayerOutputAfterReluAddress));
    // Dot product with final layer weight vector.
    predict.add(new TInstruction(TOp.VECTOR_INNER_PRODUCT_OP, kFirstLayerOutputAfterReluAddress,
        kFinalLayerWeightsAddress, TMemory.kPredictionScalarAddress));
    // Add final layer bias.
    assert kFinalLayerBiasAddress <= TMemory.numOfScalarAddresses;
    predict.add(new TInstruction(TOp.SCALAR_SUM_OP, TMemory.kPredictionScalarAddress,
        kFinalLayerBiasAddress, TMemory.kPredictionScalarAddress));

    // learn
    ArrayList<TInstruction> learn = algorithm.getLearn();
    learn.add(new TInstruction(TOp.SCALAR_DIFF_OP, TMemory.kLabelScalarAddress,
        TMemory.kPredictionScalarAddress, kPredictionErrorAddress));
    learn.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, kLearningRateAddress, kPredictionErrorAddress,
        kPredictionErrorAddress));
    assert kFinalLayerBiasAddress <= TMemory.numOfScalarAddresses;
    // Update final layer bias.
    learn.add(new TInstruction(TOp.SCALAR_SUM_OP, kFinalLayerBiasAddress, kPredictionErrorAddress,
        kFinalLayerBiasAddress));
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

    // Update first layer bias.
    learn.add(new TInstruction(TOp.VECTOR_SUM_OP, kFirstLayerBiasAddress,
        kGradientWrtActivationsAddress, kFirstLayerBiasAddress));
    learn.add(new TInstruction(TOp.VECTOR_OUTER_PRODUCT_OP, kGradientWrtActivationsAddress,
        TMemory.kFeatureVectorAddress, kGradientWrtFirstLayerWeightsAddress));
    learn.add(new TInstruction(TOp.MATRIX_SUM_OP, kFirstLayerWeightsAddress,
        kGradientWrtFirstLayerWeightsAddress, kFirstLayerWeightsAddress));
    return algorithm;
  }
}
