package methods.MGG_AV;

import java.util.ArrayList;
import core.*;
import utils.TRandomGenerator;

public class TAlgorithmMutator {
  private TAlgorithmMutation[] fAllowedActions;
  private double fMutateProb;
  private ArrayList<TComponentType> fAllowedComponents = new ArrayList<TComponentType>();
  private int fSetupSizeMin;
  private int fSetupSizeMax;
  private int fPredictSizeMin;
  private int fPredictSizeMax;
  private int fLearnSizeMin;
  private int fLearnSizeMax;
  private TRandomGenerator fRand;
  private TAllowedOpsManager fAllowedOpsManager;

  // 前回の突然変異を記録用
  private TAlgorithmMutation fActionOfPrevMutation;
  private TComponentType fComponentTypeOfPrevMutation;
  private Integer fTargetLine1OfPrevMutation;
  private Integer fTargetLine2OfPrevMutation;

  public TAlgorithmMutator(TAlgorithmMutation[] allowedActions, double mutateProb, int setupSizeMin,
      int setupSizeMax, int predictSizeMin, int predictSizeMax, int learnSizeMin, int learnSizeMax,
      TAllowedOpsManager allowedOpsManager, TRandomGenerator rand) {
    fSetupSizeMin = setupSizeMin;
    fSetupSizeMax = setupSizeMax;
    fPredictSizeMin = predictSizeMin;
    fPredictSizeMax = predictSizeMax;
    fLearnSizeMin = learnSizeMin;
    fLearnSizeMax = learnSizeMax;
    fRand = rand;

    fMutateProb = mutateProb;
    fAllowedActions = allowedActions;
    fAllowedOpsManager = allowedOpsManager;
    if (allowedOpsManager.isAllowedSetupOpsPresent())
      fAllowedComponents.add(TComponentType.SETUP);
    if (allowedOpsManager.isAllowedPredictOpsPresent())
      fAllowedComponents.add(TComponentType.PREDICT);
    if (allowedOpsManager.isAllowedLearnOpsPresent())
      fAllowedComponents.add(TComponentType.LEARN);
    if (fAllowedComponents.isEmpty())
      throw new RuntimeException("Must mutate at least one component.");
  }

  int getComponentSizeMin(TComponentType type) {
    switch (type) {
      case SETUP:
        return fSetupSizeMin;
      case PREDICT:
        return fPredictSizeMin;
      case LEARN:
        return fLearnSizeMin;
      default:
        throw new RuntimeException("Should not reach here.");
    }
  }

  int getComponentSizeMax(TComponentType type) {
    switch (type) {
      case SETUP:
        return fSetupSizeMax;
      case PREDICT:
        return fPredictSizeMax;
      case LEARN:
        return fLearnSizeMax;
      default:
        throw new RuntimeException("Should not reach here.");
    }
  }

  TComponentType getRandomMutatableComponentType() {
    int index = fRand.nextInt(0, fAllowedComponents.size());
    return fAllowedComponents.get(index);
  }

  int getRandomInstructionIndex(int componentSize) {
    return fRand.nextInt(0, componentSize);
  }

  TAlgorithmMutation getActionOfPrevMutation() {
    return fActionOfPrevMutation;
  };

  TComponentType getComponentTypeOfPrevMutation() {
    return fComponentTypeOfPrevMutation;
  };

  Integer getTargetLine1OfPrevMutation() {
    return fTargetLine1OfPrevMutation;
  };

  Integer getTargetLine2OfPrevMutation() {
    return fTargetLine2OfPrevMutation;
  };

  public TAlgorithm mutate(TAlgorithm algorithm) {
    if (fMutateProb >= 1.0 || fRand.nextProbability() < fMutateProb) {
      mutateImpl(algorithm);
    }
    return algorithm;
  }

  public TAlgorithm mutate(int numOfMutations, TAlgorithm algorithm) {
    if (fMutateProb >= 1.0 || fRand.nextProbability() < fMutateProb) {
      for (int i = 0; i < numOfMutations; i++) {
        mutateImpl(algorithm);
      }
    }
    return algorithm;
  }

  private TAlgorithm mutateImpl(TAlgorithm algorithm) {
    int actionIndex = fRand.nextInt(0, fAllowedActions.length);
    TAlgorithmMutation action = fAllowedActions[actionIndex];
    fActionOfPrevMutation = action;
    switch (action) {
      case ALTER_PARAM:
        alterParam(algorithm);
        break;
      case RANDOMIZE_INSTRUCTION:
        randomizeInstruction(algorithm);
        break;
      case RANDOMIZE_COMPONENT:
        randomizeComponent(algorithm);
        break;
      case IDENTITY:
        break;
      case INSERT_INSTRUCTION:
        insertInstruction(algorithm);
        break;
      case REMOVE_INSTRUCTION:
        removeInstruction(algorithm);
        break;
      case TRADE_INSTRUCTION:
        tradeInstruction(algorithm);
        break;
      case RANDOMIZE_ALGORITHM:
        randomizeAlgorithm(algorithm);
        break;
      // Do not add a default clause here. All actions should be supported.
    }
    return algorithm;
  }

  TAlgorithm alterParam(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    ArrayList<TInstruction> component = algorithm.getComponent(type);
    if (!component.isEmpty()) {
      int index = getRandomInstructionIndex(component.size());
      fTargetLine1OfPrevMutation = index + 1;
      fTargetLine2OfPrevMutation = null;
      TInstruction instr = component.get(index);
      TInstructionRandomizer.alterParam(instr, fRand);
    }
    return algorithm;
  }

  TAlgorithm randomizeInstruction(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    ArrayList<TInstruction> component = algorithm.getComponent(type);
    if (!component.isEmpty()) {
      int index = getRandomInstructionIndex(component.size());
      fTargetLine1OfPrevMutation = index + 1;
      fTargetLine2OfPrevMutation = null;
      TOp op = fAllowedOpsManager.getRandomOp(type, fRand);
      TInstructionRandomizer.setOpAndRandomize(component.get(index), op, fRand);
    }
    return algorithm;
  }

  TAlgorithm randomizeComponent(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    fTargetLine1OfPrevMutation = null;
    fTargetLine2OfPrevMutation = null;
    TComponentRandomizer.execute(algorithm, type, fAllowedOpsManager, fRand);
    return algorithm;
  }

  TAlgorithm insertInstruction(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    ArrayList<TInstruction> component = algorithm.getComponent(type);
    if (component.size() >= getComponentSizeMax(type))
      return algorithm;
    TOp op = fAllowedOpsManager.getRandomOp(type, fRand);
    int position = insertInstructionUnconditionally(op, component);
    fTargetLine1OfPrevMutation = position + 1;
    fTargetLine2OfPrevMutation = null;
    return algorithm;
  }

  TAlgorithm removeInstruction(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    ArrayList<TInstruction> component = algorithm.getComponent(type);
    if (component.size() <= getComponentSizeMin(type))
      return algorithm;
    int position = removeInstructionUnconditionally(component);
    fTargetLine1OfPrevMutation = position + 1;
    fTargetLine2OfPrevMutation = null;
    return algorithm;
  }

  TAlgorithm tradeInstruction(TAlgorithm algorithm) {
    TComponentType type = getRandomMutatableComponentType();
    fComponentTypeOfPrevMutation = type;
    ArrayList<TInstruction> component = algorithm.getComponent(type);
    TOp op = fAllowedOpsManager.getRandomOp(type, fRand);
    int position1 = insertInstructionUnconditionally(op, component);
    int position2 = removeInstructionUnconditionally(component);
    fTargetLine1OfPrevMutation = position1 + 1;
    fTargetLine2OfPrevMutation = position2 + 1;
    return algorithm;
  }

  TAlgorithm randomizeAlgorithm(TAlgorithm algorithm) {
    fComponentTypeOfPrevMutation = null;
    fTargetLine1OfPrevMutation = null;
    fTargetLine2OfPrevMutation = null;
    if (fAllowedOpsManager.isAllowedSetupOpsPresent())
      TComponentRandomizer.execute(algorithm, TComponentType.SETUP, fAllowedOpsManager, fRand);
    if (fAllowedOpsManager.isAllowedPredictOpsPresent())
      TComponentRandomizer.execute(algorithm, TComponentType.PREDICT, fAllowedOpsManager, fRand);
    if (fAllowedOpsManager.isAllowedLearnOpsPresent())
      TComponentRandomizer.execute(algorithm, TComponentType.LEARN, fAllowedOpsManager, fRand);
    return algorithm;
  }

  private int insertInstructionUnconditionally(TOp op, ArrayList<TInstruction> component) {
    int position = getRandomInstructionIndex(component.size() + 1);
    TInstruction instr = TInstructionRandomizer.makeInstructionAndRandomize(op, fRand);
    component.add(position, instr);
    return position;
  }

  private int removeInstructionUnconditionally(ArrayList<TInstruction> component) {
    int position = getRandomInstructionIndex(component.size());
    component.remove(position);
    return position;
  }
}
