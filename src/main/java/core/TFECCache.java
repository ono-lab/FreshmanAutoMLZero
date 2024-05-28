package core;

import utils.TLRUCache;

class TCachedEvaluation {
  private double fFitness;
  private int fCount = 0;

  TCachedEvaluation(double fitness) {
    fFitness = fitness;
  }

  void countUp() {
    fCount++;
  }

  int getCount() {
    return fCount;
  }

  double getFitness() {
    return fFitness;
  }
};

/**
 * 評価値をキャッシュするためのクラス．タスクの学習データの一部と検証データの一部を用いて，各データに対する予測値を求める．その後，それぞれの予測値のエラーを組にしてハッシュ化を行い，ハッシュが同じ場合に評価値の再計算をしないようにする．
 */
public class TFECCache {
  private TFECSpec fSpec;
  private TLRUCache<Long, TCachedEvaluation> fCache;
  private int fGeneration = 0;

  public TFECCache(TFECSpec spec) {
    assert spec.numOfTrainExamples > 0;
    assert spec.numOfValidExamples > 0;
    assert spec.cacheSize > 1;
    assert spec.forgetEvery == 0 || spec.forgetEvery > 1;
    fSpec = spec;
    fCache = new TLRUCache<Long, TCachedEvaluation>(spec.cacheSize);
  }

  public void incrementGeneration() {
    fGeneration++;
  }

  /**
   * キャッシュを作成するために行う事前学習の回数を取得する関数．
   */
  public int getNumOfTrainExamples() {
    return fSpec.numOfTrainExamples;
  }

  /**
   * キャッシュを作成するために行う検証の回数を取得する関数．
   */
  public int getNumOfValidExamples() {
    return fSpec.numOfValidExamples;
  }

  /**
   * ハッシュ値hashに対応するfitnessをキャッシュする関数．
   */
  public void put(final long hash, final double fitness) {
    fCache.put(hash, new TCachedEvaluation(fitness));
  }

  /**
   * キャッシュされた評価値を返却する関数．ただし，キャッシュされていない場合は，NANが返却される．
   */
  public double getCachedFitness(long hash) {
    TCachedEvaluation cached = fCache.get(hash);
    if (cached == null)
      return Double.NaN;

    // キャッシュの参照回数が上限を超えたらキャッシュを消去する
    cached.countUp();
    if (fSpec.forgetEvery != 0 && cached.getCount() >= fSpec.forgetEvery) {
      fCache.remove(hash);
    }

    return cached.getFitness();
  }

  /**
   * キャッシュを消去する関数．
   */
  public void clear() {
    fCache.clear();
  }
}
