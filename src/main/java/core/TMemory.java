package core;

import jp.ac.titech.onolab.core.matrix.TCMatrix;
import utils.TMathUtility;

/**
 * TInstructionやTAlgorithmを実行する上で使用するメモリー
 */
public class TMemory {
  // ==============================
  // スカラーのメモリーマッピング
  // ==============================
  // 教師ラベルを代入する用
  public static final int kLabelScalarAddress = 0;
  // アルゴリズムの結果（ラベルの予測値）を代入する用
  public static final int kPredictionScalarAddress = 1;
  // アルゴリズムや命令による出力先として指定アドレスの最小値
  // この最小値未満のアドレスは命令への入力先としてのみ指定可
  // この最小値以降のアドレスは命令への入力，命令から出力のどちらとしても指定可
  public static final int kFirstOutScalarAddress = 1;
  // 入力および出力で指定できるアドレスの数
  // デフォルトとして20を設定し，実験を走らせる時に外部から変更する
  public static int numOfScalarAddresses = 20;

  public static final int kFirstInScalarAddressOfPredict = 2;

  // ==============================
  // ベクトルのメモリーマッピング
  // ==============================
  // アルゴリズムの入力である特徴ベクトルを入れるアドレス
  public static final int kFeatureVectorAddress = 0;
  // スカラーのときと同様
  public static final int kFirstOutVectorAddress = 1;
  public static int numOfVectorAddresses = 20;
  // ベクトルを予測ラベル，教師ラベルとして使用するときは以下をコメントアウト
  // 現状，TInstructionやTAlgorithmは対応していないので注意
  // static final int kLabelVectorAddress = 1;
  // static final int kPredictionVectorAddress = 2;
  // static final int kFirstOutVectorAddress = 3;

  // ==============================
  // 行列のメモリーマッピング
  // ==============================
  // スカラーのときと同様
  public static final int kFirstOutMatrixAddress = 0;
  public static int numOfMatrixAddresses = 20;

  private int fDim;

  // TInstructionExecutorを簡潔にするために，以下はコーディング規約に従わない．
  public final double[] scalar = new double[numOfScalarAddresses];
  public final TCMatrix[] vector = new TCMatrix[numOfVectorAddresses];
  public final TCMatrix[] matrix = new TCMatrix[numOfMatrixAddresses];

  /**
   * 次元dimでベクトルや行列を0で初期化したメモリーを生成するコンストラクタ．メモリー内に異なる次元のベクトルや行列を代入することは出来ない．
   */
  public TMemory(final int dim) {
    fDim = dim;
    for (int index = 0; index < vector.length; index++) {
      vector[index] = new TCMatrix(fDim);
    }
    for (int index = 0; index < matrix.length; index++) {
      matrix[index] = new TCMatrix(fDim, fDim);
    }
  }

  /**
   * thisのベクトルや行列の次元を返却する関数
   */
  public int getDim() {
    return fDim;
  }

  /**
   * thisのベクトルや行列の次元を変更する関数．dimが同じ場合は何も処理しない．
   */
  public void changeDim(int dim) {
    if (dim == fDim) {
      return;
    }
    for (int index = 0; index < vector.length; index++) {
      if (dim != fDim) {
        vector[index] = new TCMatrix(dim);
      }
    }
    for (int index = 0; index < matrix.length; index++) {
      matrix[index] = new TCMatrix(dim, dim);
    }
    fDim = dim;
  }

  /**
   * メモリーをすべて0.0で初期化する関数
   */
  public void wipe() {
    for (int index = 0; index < scalar.length; index++) {
      scalar[index] = 0.0;
    }
    for (TCMatrix vector : vector) {
      vector.fill(0.0);
    }
    for (TCMatrix matrix : matrix) {
      matrix.fill(0.0);
    }
  }

  /**
   * 特徴ベクトルをメモリーにセットする関数
   */
  public void setFeatureVector(TCMatrix feature) {
    assert feature.getRowDimension() == getDim();
    assert feature.getColumnDimension() == 1;
    vector[kFeatureVectorAddress] = feature;
  }

  /**
   * （アルゴリズムによってセットされた）予測値を取得する関数
   */
  public double getPredictionScalar() {
    return scalar[kPredictionScalarAddress];
  }

  /**
   * 予測値にsigmoid関数を適用して0から1にスケーリング（確率化）する関数．
   */
  public void convertPredictionToProbability() {
    scalar[kPredictionScalarAddress] = TMathUtility.sigmoid(scalar[kPredictionScalarAddress]);
  }

  /**
   * 教師ラベルをセットする関数
   */
  public void setLabelScalar(double label) {
    scalar[kLabelScalarAddress] = label;
  }

  /**
   * 教師ラベルを初期化する関数
   */
  public void resetLabel() {
    scalar[kLabelScalarAddress] = 0.0;
  }

  // public TCMatrix getPredictionVector() {
  // return vector[kPredictionVectorAddress];
  // }

  // public void setLabelVector(TCMatrix label) {
  // assert label.getRowDimension() == getDim();
  // assert label.getColumnDimension() == 1;
  // vector[kLabelVectorAddress] = label;
  // }

  @Override
  public String toString() {
    String str = "Scalar\n";
    for (int index = 0; index < scalar.length; index++) {
      str += index;
      if (index == kLabelScalarAddress) {
        str += "(label)";
      }
      if (index == kPredictionScalarAddress) {
        str += "(prediction)";
      }
      str += ":\n" + scalar[index] + "\n";
    }
    str += "\n\nVector\n";
    for (int index = 0; index < vector.length; index++) {
      str += index;
      if (index == kFeatureVectorAddress) {
        str += "(feature)";
      }
      str += ":\n" + vector[index];

    }
    str += "\n\nMatrix\n";
    for (int index = 0; index < matrix.length; index++) {
      str += index;
      str += ":\n" + matrix[index];
    }
    return str;
  }
}
