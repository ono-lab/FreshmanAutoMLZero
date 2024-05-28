package core;

import java.util.ArrayList;
import utils.TArrayUtility;
import utils.TRandomGenerator;

public class TRequiredVariables {
  private ArrayList<Integer> fAvailableIndexes = new ArrayList<Integer>();
  private TVariable[] fVariables;
  private Integer fSize = 0;

  public Integer getRandomAvailableIndex(TRandomGenerator fRand) {
    if (fAvailableIndexes.size() == 0) {
      return null;
    }
    int random = fRand.nextInt(fAvailableIndexes.size());
    return fAvailableIndexes.get(random);
  }

  public void assignVariable(Integer index, TVariable variable) {
    boolean isFound = fAvailableIndexes.remove((Object) index);
    if (!isFound)
      throw new RuntimeException("Unexpected error has occurred.");
    if (fVariables[2 * index] == null) {
      fVariables[2 * index] = variable;
    } else if (fVariables[2 * index + 1] == null) {
      fVariables[2 * index + 1] = variable;
    } else {
      throw new RuntimeException("Unexpected error has occurred.");
    }
  }

  public void fill(int index) {
    fAvailableIndexes.remove((Object) index);
    fAvailableIndexes.remove((Object) index);
  }

  public void initialize(int size) {
    fAvailableIndexes.clear();
    for (int index = 0; index < size; index++) {
      fAvailableIndexes.add(index);
      fAvailableIndexes.add(index);
    }
    fVariables = new TVariable[size * 2];
    for (int index = 0; index < fVariables.length; index++) {
      fVariables[index] = null;
    }
    fSize = size;
  }

  public void reset() {
    assert fSize != null;
    fAvailableIndexes.clear();
    for (int index = 0; index < fSize; index++) {
      fAvailableIndexes.add(index);
      fAvailableIndexes.add(index);
    }
  }

  public void copyFrom(TRequiredVariables other) {
    fSize = other.fSize;
    fAvailableIndexes.clear();
    fAvailableIndexes.addAll(other.fAvailableIndexes);
    fVariables = new TVariable[fSize * 2];
    for (int index = 0; index < fVariables.length; index++) {
      fVariables[index] = other.fVariables[index];
    }
  }

  /**
   * targetよりも後（コンポーネントで考えたときは前）のアサインをクリアして、リセットして再度割り当てを可能にする関数
   * 
   * @param target
   * @return
   */
  public ArrayList<TVariable> clearAssignedVariablesAndResetAfter(int target) {
    assert fSize != null;
    ArrayList<TVariable> canceled = new ArrayList<TVariable>();
    fAvailableIndexes.clear();
    for (int index = target; index < fSize; index++) {
      if (fVariables[2 * index] != null) {
        canceled.add(fVariables[2 * index]);
        fVariables[2 * index] = null;
      }
      if (fVariables[2 * index + 1] != null) {
        canceled.add(fVariables[2 * index + 1]);
        fVariables[2 * index + 1] = null;
      }
      fAvailableIndexes.add(index);
      fAvailableIndexes.add(index);
    }
    return canceled;
  }

  public Integer randomAssignVariable(TRandomGenerator fRand, TVariable variable) {
    Integer randomIndex = getRandomAvailableIndex(fRand);
    if (randomIndex == null) {
      return null;
    }
    assignVariable(randomIndex, variable);
    return randomIndex;
  }

  public TVariable get(int index, int number) {
    assert number == 1 || number == 2;
    return fVariables[2 * index + number - 1];
  }

  public boolean hasAvailableIndex() {
    return fAvailableIndexes.size() > 0;
  }

  @Override
  public String toString() {
    String str = "";
    for (Integer i = 0; i < fVariables.length / 2; i++) {
      str += (i.toString()) + ". " + fVariables[2 * i] + ", " + fVariables[2 * i + 1];
      if (TArrayUtility.include(fAvailableIndexes, i)) {
        str += " (available)";
      }
      str += "\n";
    }
    return str;
  }
}
