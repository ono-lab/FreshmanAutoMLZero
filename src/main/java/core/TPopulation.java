package core;

import java.util.ArrayList;
import java.util.Objects;
import utils.TRandomGenerator;
import utils.TArrayUtility;

public class TPopulation<T extends TIndividualBase> {
  private ArrayList<T> fIndividuals = new ArrayList<T>();

  public int size() {
    return fIndividuals.size();
  }

  public ArrayList<T> getAll() {
    return fIndividuals;
  }

  public T get(int index) {
    return fIndividuals.get(index);
  }

  public T remove(int index) {
    return fIndividuals.remove(index);
  }

  public T randomRemove(TRandomGenerator rand) {
    int index = rand.nextInt(fIndividuals.size());
    return remove(index);
  }

  public boolean add(T individual) {
    return fIndividuals.add(individual);
  }

  public T getRandom(TRandomGenerator rand) {
    int index = rand.nextInt(size());
    return get(index);
  }

  public T getBest() {
    assert size() > 0;
    T best = fIndividuals.get(0);
    for (T individual : fIndividuals) {
      if (best.getFitness() < individual.getFitness()) {
        best = individual;
      }
    }
    return best;
  }

  public T getTournamentSelected(int tournamentSize, TRandomGenerator rand) {
    double tournamentBestFitness = Double.NEGATIVE_INFINITY;
    int bestIndex = -1;
    int[] indexArray = rand.nextUniqueIntegerArray(size());
    for (int tournamentIndex = 0; tournamentIndex < tournamentSize; tournamentIndex++) {
      int index = indexArray[tournamentIndex];
      double currentFitness = get(index).getFitness();
      if (currentFitness > tournamentBestFitness) {
        tournamentBestFitness = currentFitness;
        bestIndex = index;
      }
    }
    assert bestIndex != -1;
    return fIndividuals.get(bestIndex);
  }

  public TPopulationStats<T> getStats() {
    int size = size();
    assert size > 0;
    double total = 0.0;
    double totalSquares = 0.0;
    T best = fIndividuals.get(0);
    for (T individual : fIndividuals) {
      double fitness = individual.getFitness();
      if (fitness > best.getFitness()) {
        best = individual;
      }
      total += fitness;
      totalSquares += fitness * fitness;
    }
    double mean = total / (double) size;
    double var = totalSquares / (double) size - mean * mean;
    if (var < 0.0)
      var = 0.0;
    double stdev = Math.sqrt(var);
    return new TPopulationStats<T>(mean, stdev, best);
  }

  public ArrayList<T> getElites(int num) {
    ArrayList<T> elites = new ArrayList<T>();
    for (int i = 0; i < num; i++) {
      T elite = null;
      for (T individual : fIndividuals) {
        if (TArrayUtility.find(elites, individual) != null)
          continue;
        if (elite == null || elite.getFitness() < individual.getFitness()) {
          elite = individual;
        }
      }
      elites.add(elite);
    }
    assert elites.size() == num;
    return elites;
  }

  public void showAllFitnesses() {
    String str = "";
    for (T individual : fIndividuals) {
      str += individual.getFitness() + "\n";
    }
    System.out.println(str);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof TPopulation)) {
      return false;
    }
    TPopulation otherPopulation = (TPopulation) other;
    if (otherPopulation.size() != size()) {
      return false;
    }
    for (int index = 0; index < size(); index++) {
      if (!Objects.equals(fIndividuals.get(index), otherPopulation.get(index))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    String str = "";
    for (T individual : fIndividuals) {
      str += individual + "\n";
    }
    str += "\n";
    return str;
  }
}
