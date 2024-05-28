package methods.MGG_AV;

import core.TAlgorithm;
import core.TIndividualBase;

public class TIndividual extends TIndividualBase {
  TIndividual(TAlgorithm algorithm) {
    super(algorithm);
  }

  public TIndividual(TAlgorithm algorithm, double fitness) {
    super(algorithm, fitness);
  }

  public TIndividual(TIndividual other) {
    super(other);
  }

  @Override
  public TIndividual clone() {
    return new TIndividual(this);
  }
}
