package task_factory.spec;

import core.TTaskBuffer;
import task_factory.creator.TProjectedBinaryClassificationTaskCreator;

// A projected binary classification task. These use pre-generated datasets.
// The following TProjectedBinaryClassificationTaskSpec fields are restricted to the given values:
// evalMethod: ACCURACY.
// numOfTrainExamples: the value should be an integer in (0, 8000]
// numOfValidExamples: the value should be an integer in (0, 1000]
// numOfTestExamples: the value should be an integer in (0, 1000]
// paramSeeds: the paramSeeds are not used so doesn't matter.
// Below are the supported choices for dataset_name, features_size,
// min_supported_data_seed, max_supported_data_seed and use_downsampling:
// |dataset_name|features_size|min/max_supported_data_seed|
// |------------|-------------|---------------------------|
// |mnist |16 |0 / 100 |
// |cifar10 |16 |0 / 100 |
//
// Meta-train / meta-validation / meta-test split:
// Since some positive-negative pairs are heldout,
// you can use all the seeds during search (meta-train) and use the heldout
// pairs in model selection and evaluation (meta-validation and meta-test).
// Among all 45 possible pairs, we recommend that the following
// 9 randomly selected pairs be held out for meta-validation and meta-test:
// (4, 6), (3, 5), (8, 9), (3, 8), (0, 9), (2, 9), (1, 8), (3, 6), (0, 5).
// If transferring to the original feature size is used as final evaluation
// (meta-test), you can use all the heldout pairs as meta-validation.
// If no transferring is used, you can use the first 4 pairs as
// meta-validation and the rest 5 pairs as meta-test.


/**
 * See `TProjectedBinaryClassificationTaskSpec` file.
 */
public class TProjectedBinaryClassificationTaskSpec extends TTaskSpec {

  // Below are the IDs for the positive and negative classes, you should
  // either specify:
  // (1) both of them, in this case, the given positive and negative classes
  // will be used;
  // (2) none of them, in this case, the positive and negative classes will
  // be randomly chosen based on the dataSeed.
  //
  // Both values should be integers in [0, 9] with the `classPair.first` smaller
  // than the `classPair.second`.
  public TClassPair classPair;

  // Name to specify the dataset to use, currently supporting "mnist" and "cifar10".
  public String datasetName;

  // Pairs to hold out when randomizing the dataset.
  // ランダムにdatasetを作る時に使わないペアをまとめる．
  public TClassPair[] heldOutPairs;

  // The path will be used as the path to the folder containing all the serialized data.
  public String path;

  // Minimum (incl.) and maximum (excl.) data seeds supported in the stable
  // that saves the dumped projected dataset.
  //
  // Only seeds in the range specified in the table above are supported.
  // The seed is obtained by mapping `dataSeed` into the range with
  // seed = (dataSeed % (maxSupportedDataSeed - minSupportedDataSeed) +
  // minSupportedDataSeed)
  // (1) when the dataset is not randomized, the specified `positiveClass` and
  // `negativeClass` will be used;
  // (2) when the dataset is randomized, i.e., when `positiveClass` and
  // `negativeClass` are not set, the `dataSeed` is also used
  // to randomly select a the positive and negative classes.
  public int minSupportedDataSeed = 0;
  public int maxSupportedDataSeed = 10;

  public TTaskBuffer createOneTaskBuffer(long paramSeed, long dataSeed) {
    return TProjectedBinaryClassificationTaskCreator.execute(this, paramSeed, dataSeed);
  };
}
