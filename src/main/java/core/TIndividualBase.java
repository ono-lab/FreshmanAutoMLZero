package core;

import java.util.Objects;

public class TIndividualBase {
  private double fFitness = Double.NaN;
  public TAlgorithm fAlgorithm;

  public TIndividualBase(TAlgorithm algorithm) {
    fAlgorithm = algorithm;
  }

  public TIndividualBase(TAlgorithm algorithm, double fitness) {
    fAlgorithm = algorithm;
    fFitness = fitness;
  }

  public void setFitness(double fitness) {
    fFitness = fitness;
  }

  public TAlgorithm getAlgorithm() {
    return fAlgorithm;
  }

  public double getFitness() {
    return fFitness;
  }

  public TIndividualBase copyFrom(TIndividualBase other) {
    fAlgorithm = new TAlgorithm(other.fAlgorithm);
    fFitness = other.fFitness;
    return this;
  }

  public TIndividualBase(TIndividualBase other) {
    copyFrom(other);
  }

  @Override
  public TIndividualBase clone() {
    return new TIndividualBase(this);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof TIndividualBase)) {
      return false;
    }
    TIndividualBase otherIndividualBase = (TIndividualBase) other;
    return Objects.equals(otherIndividualBase.fAlgorithm, fAlgorithm)
        && Objects.equals(otherIndividualBase.fFitness, fFitness);
  }

  @Override
  public String toString() {
    return fAlgorithm + "fitness:" + fFitness + "\n";
  }
}
