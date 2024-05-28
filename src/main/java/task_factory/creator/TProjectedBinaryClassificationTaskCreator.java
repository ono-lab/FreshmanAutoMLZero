package task_factory.creator;

import java.util.ArrayList;
import core.TEvalMethod;
import core.TTaskBuffer;
import task_factory.spec.TClassPair;
import task_factory.spec.TProjectedBinaryClassificationTaskSpec;
import utils.THashGenerator;
import utils.TRandomGenerator;
import utils.TArrayUtility;

public class TProjectedBinaryClassificationTaskCreator {
  public static TTaskBuffer execute(TProjectedBinaryClassificationTaskSpec spec, long pramSeed,
      long dataSeed) {
    int numOfTrainExamples = spec.numOfTrainExamples;
    int numOfValidExamples = spec.numOfValidExamples;
    int featureSize = spec.featuresSize;
    TEvalMethod evalMethod = spec.evalMethod;

    assert featureSize > 0;
    assert numOfTrainExamples > 0;
    assert numOfValidExamples > 0;
    assert evalMethod == TEvalMethod.ACCURACY;

    TTaskBuffer buffer = new TTaskBuffer(featureSize);
    buffer.setEvalMethod(evalMethod);

    String path = spec.path;
    if (path == null) {
      throw new RuntimeException("You have to specify the path to the data!");
    }

    TClassPair selectedPair = null;

    if (spec.classPair != null) {
      selectedPair = spec.classPair;
      int numOfSupportedDataSeeds = spec.maxSupportedDataSeed - spec.minSupportedDataSeed;
      dataSeed = spec.minSupportedDataSeed + dataSeed % numOfSupportedDataSeeds;
    } else {
      TRandomGenerator rand =
          new TRandomGenerator(THashGenerator.getCombinedHash(dataSeed, 856572777L));

      ArrayList<TClassPair> searchList = new ArrayList<TClassPair>();
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
          TClassPair classPair = new TClassPair(i, j);
          if (TArrayUtility.find(spec.heldOutPairs, classPair) == null) {
            searchList.add(classPair);
          }
        }
      }
      assert searchList.size() > 0;
      selectedPair = searchList.get(rand.nextInt(searchList.size()));
      dataSeed = rand.nextInt(spec.minSupportedDataSeed, spec.maxSupportedDataSeed);
    }

    // Generate the key using the task spec.
    String filename = "binary_" + spec.datasetName + "-pos_" + selectedPair.first + "-neg_"
        + selectedPair.second + "-dim_" + featureSize + "-seed_" + dataSeed;
    String fullPath = path + "/" + filename;

    TScalarLabelDatasetLoader loader = new TScalarLabelDatasetLoader();
    loader.load(fullPath);
    loader.fillTaskBuffer(buffer);

    return buffer;
  }
}
