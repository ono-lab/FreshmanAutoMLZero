package methods.RE;

import core.TAlgorithm;
import core.TIndividualBase;

public class TIndividual extends TIndividualBase {
  TIndividual(TAlgorithm algorithm) {
    super(algorithm);
  }

  public TIndividual(TAlgorithm algorithm, double fitness) {
    super(algorithm, fitness);
  }
}
