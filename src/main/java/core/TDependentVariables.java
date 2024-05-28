package core;

import java.util.ArrayList;
import utils.TArrayUtility;
import utils.TRandomGenerator;

public class TDependentVariables {
  private ArrayList<TVariable> fVariables = new ArrayList<TVariable>();
  private ArrayList<TVariable> fScalarVariables = new ArrayList<TVariable>();
  private ArrayList<TVariable> fVectorVariables = new ArrayList<TVariable>();
  private ArrayList<TVariable> fMatrixVariables = new ArrayList<TVariable>();
  private ArrayList<TMemoryType> fAvailableMemoryTypes = new ArrayList<TMemoryType>();

  public TDependentVariables(){}

  public TDependentVariables(TDependentVariables other) {
    fVariables.addAll(other.fVariables);
    fScalarVariables.addAll(other.fScalarVariables);
    fVectorVariables.addAll(other.fVectorVariables);
    fMatrixVariables.addAll(other.fMatrixVariables);
    fAvailableMemoryTypes.addAll(other.fAvailableMemoryTypes);
  }

  private void updateAvailableMemoryTypes(){
    fAvailableMemoryTypes.clear();
    if (fScalarVariables.size() > 0) {
      fAvailableMemoryTypes.add(TMemoryType.SCALAR);
    }
    if (fVectorVariables.size() > 0) {
      fAvailableMemoryTypes.add(TMemoryType.VECTOR);
    }
    if (fMatrixVariables.size() > 0) {
      fAvailableMemoryTypes.add(TMemoryType.MATRIX);
    }
  }

  public void add(TVariable variable) {
    if (!TArrayUtility.include(fVariables, variable)) {
      fVariables.add(variable);
      switch (variable.getMemoryType()) {
        case SCALAR:
          fScalarVariables.add(variable);
          break;
        case VECTOR:
          fVectorVariables.add(variable);
          break;
        case MATRIX:
          fMatrixVariables.add(variable);
          break;
      }
      updateAvailableMemoryTypes();
    }
  }

  public void addAll(ArrayList<TVariable> variables){
    for (TVariable variable : variables) {
      add(variable);
    }
  }

  public void remove(TVariable variable) {
    fVariables.remove(variable);
    switch (variable.getMemoryType()) {
      case SCALAR:
        fScalarVariables.remove(variable);
        break;
      case VECTOR:
        fVectorVariables.remove(variable);
        break;
      case MATRIX:
        fMatrixVariables.remove(variable);
        break;
    }
    updateAvailableMemoryTypes();
  }

  public TVariable randomRemove(TRandomGenerator fRand) {
    int index = fRand.nextInt(fVariables.size());
    TVariable variable = fVariables.remove(index);
    switch (variable.getMemoryType()) {
      case SCALAR:
        fScalarVariables.remove(variable);
        break;
      case VECTOR:
        fVectorVariables.remove(variable);
        break;
      case MATRIX:
        fMatrixVariables.remove(variable);
        break;
    }
    updateAvailableMemoryTypes();
    return variable;
  }

  public TVariable randomRemove(TRandomGenerator fRand, TMemoryType memoryType) {
    TVariable variable = null;
    switch (memoryType) {
      case SCALAR: {
        int index = fRand.nextInt(fScalarVariables.size());
        variable = fScalarVariables.remove(index);
        break;
      }
      case VECTOR: {
        int index = fRand.nextInt(fVectorVariables.size());
        variable = fVectorVariables.remove(index);
        break;
      }
      case MATRIX: {
        int index = fRand.nextInt(fMatrixVariables.size());
        variable = fMatrixVariables.remove(index);
        break;
      }
    }
    if (variable == null)
      return null;

    fVariables.remove(variable);
    updateAvailableMemoryTypes();
    return variable;
  }

  public ArrayList<TMemoryType> getAvailableMemoryTypes() {
    return fAvailableMemoryTypes;
  }

  public ArrayList<TVariable> copyVariables() {
    ArrayList<TVariable> variables = new ArrayList<TVariable>();
    for (TVariable variable : fVariables) {
      variables.add(variable);
    }
    return variables;
  }

  public ArrayList<TVariable> getVariables() {
    return fVariables;
  }

  public boolean isBlank() {
    return fVariables.size() == 0;
  }

  public void initialize() {
    fVariables.clear();
    fScalarVariables.clear();
    fVectorVariables.clear();
    fMatrixVariables.clear();
    fAvailableMemoryTypes.clear();
  }

  public void initializeBy(ArrayList<TVariable> variables) {
    initialize();
    addAll(variables);
  }

  @Override
  public String toString() {
    String str = "";
    for (TVariable variable : fVariables) {
      str += variable + ", ";
    }
    return str;
  }
}
