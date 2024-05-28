package core;

import java.util.Objects;

public class TPopulationStats<T extends TIndividualBase> {
  private double fMean;
  private double fStdev;
  private T fBest;

  TPopulationStats(double mean, double stdev, T best) {
    fMean = mean;
    fStdev = stdev;
    fBest = best;
  }

  public double getMean() {
    return fMean;
  }

  public double getStdev() {
    return fStdev;
  }

  public T getBestIndividual() {
    return fBest;
  }

  public TAlgorithm getBestAlgorithm() {
    return fBest.getAlgorithm();
  }

  public double getBestFitness() {
    return fBest.getFitness();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof TPopulationStats)) {
      return false;
    }
    TPopulationStats otherPopulationStats = (TPopulationStats) other;
    return Objects.equals(otherPopulationStats.fMean, fMean)
        && Objects.equals(otherPopulationStats.fStdev, fStdev)
        && Objects.equals(otherPopulationStats.fBest, fBest);
  }
}
