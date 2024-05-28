package core;

import models.TModel;
import models.TNeuralNetModel;

public class TTrainBudgetSpec {
  /**
   * 基準となるアルゴリズムを生成するモデル
   */
  public TModel model = new TNeuralNetModel();

  /**
   * 基準アルゴリズムの何倍まで許すかの値
   */
  public double trainBudgetThresholdFactor = 2.0;

}
