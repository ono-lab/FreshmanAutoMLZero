package methods.RE;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TLinearRegressionExperiment {
  public static void main(String[] args) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    TExperimentSpec spec = objectMapper.readValue(
        new File("./src/main/java/methods/RE/linear_regression.json"),
        TExperimentSpec.class);
    TExperimentRunner.execute(spec);
  }
}
