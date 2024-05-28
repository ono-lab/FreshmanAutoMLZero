package task_factory.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.protobuf.InvalidProtocolBufferException;
import core.TTaskBuffer;
import jp.ac.titech.onolab.core.matrix.TCMatrix;
import proto.Dataset.FeatureVector;
import proto.Dataset.ScalarLabelDataset;

public class TScalarLabelDatasetLoader {
  private String fFullPath;
  private boolean fHasLoaded = false;
  private ScalarLabelDataset fSavedDataset;

  public void load(String fullPath) {
    assert !fHasLoaded;
    Path file = Paths.get(fFullPath);
    try {
      byte[] bytes = Files.readAllBytes(file);
      fSavedDataset = ScalarLabelDataset.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("Error while parsing the proto from " + fFullPath);
    } catch (IOException e) {
      throw new RuntimeException("No data found at " + fFullPath
          + ". Please follow the README to generate the projected binary datasets first.");
    }
    assert fSavedDataset != null;
    fHasLoaded = true;
  }

  private TCMatrix transform(FeatureVector protoFeatureVector) {
    int dim = protoFeatureVector.getElementsCount();
    TCMatrix feature = new TCMatrix(dim);
    for (int i = 0; i < dim; i++) {
      feature.setValue(i, protoFeatureVector.getElements(i));
    }
    return feature;
  }

  public void fillTaskBuffer(TTaskBuffer buffer) {
    assert fHasLoaded;
    assert fSavedDataset.getTrainFeaturesCount() == fSavedDataset.getTrainLabelsCount();
    assert fSavedDataset.getValidFeaturesCount() == fSavedDataset.getValidLabelsCount();

    int numOfTrainData = fSavedDataset.getTrainFeaturesCount();
    int numOfValidData = fSavedDataset.getValidFeaturesCount();

    // Train data
    for (int i = 0; i < numOfTrainData; i++) {
      double label = fSavedDataset.getTrainLabels(i);
      TCMatrix feature = transform(fSavedDataset.getTrainFeatures(i));
      buffer.addTrainData(feature, label);
    }

    // Valid data
    for (int i = 0; i < numOfValidData; i++) {
      double label = fSavedDataset.getValidLabels(i);
      TCMatrix feature = transform(fSavedDataset.getValidFeatures(i));
      buffer.addValidData(feature, label);
    }
  }
}
