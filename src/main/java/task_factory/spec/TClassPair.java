package task_factory.spec;

import utils.TPair;

public class TClassPair extends TPair<Integer, Integer> {
  public TClassPair(Integer i, Integer j) {
    super(Math.min(i, j), Math.max(i, j));
  }
}
