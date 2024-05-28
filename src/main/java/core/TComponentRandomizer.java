package core;

import utils.TRandomGenerator;

public class TComponentRandomizer {
  /**
   * Randomizes all the instructions in the component function. Does not change the component
   * function size.
   */
  public static TAlgorithm execute(TAlgorithm algorithm, TComponentType componentType,
      TAllowedOpsManager allowedOpsManager, TRandomGenerator rand) {
    assert allowedOpsManager.isAllowedOpsPresent(componentType);
    for (TInstruction instr : algorithm.getComponent(componentType)) {
      TOp op = allowedOpsManager.getRandomOp(componentType, rand);
      TInstructionRandomizer.setOpAndRandomize(instr, op, rand);
    }
    return algorithm;
  }

  public static TAlgorithm executeAll(TAlgorithm algorithm, TAllowedOpsManager allowedOpsManager,
      TRandomGenerator rand) {
    execute(algorithm, TComponentType.SETUP, allowedOpsManager, rand);
    execute(algorithm, TComponentType.PREDICT, allowedOpsManager, rand);
    execute(algorithm, TComponentType.LEARN, allowedOpsManager, rand);
    return algorithm;
  }
}
