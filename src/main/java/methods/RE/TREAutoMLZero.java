package methods.RE;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import core.TAlgorithm;
import core.TEvaluator;
import core.TPopulation;
import core.TPopulationStats;
import file_templates.TMutationAnalysis;
import models.TModel;
import utils.TRandomGenerator;

public class TREAutoMLZero {
  private static final long kMillisPerSecond = 1000;
  private static final String kCSVLogDir = "./logs/RE/";

  // 実験ログのCSV出力
  private static final boolean kCSVLogOutput = true;
  private FileWriter fCSVLogOutputFW;

  // 突然変異の記録の保存の有無
  private static final boolean kMutationOutput = false;
  private FileWriter fMutationOutputFW;

  private int fTrialNo;
  private String fLogPrefix;

  // Runs up to this many total individuals.
  private int fPopulationSize;
  private TPopulation<TIndividual> fPopulation;

  private int fTournamentSize;

  // The mutator to use to perform all mutations.
  private TAlgorithmMutator fMutator;

  private TEvaluator fEvaluator;

  // 初期化用のパラメータ
  private boolean fIsInitialized = false;
  private TModel fInitModel;

  // How frequently to print progress reports.
  private int fProgressEvery;

  private long fStartSecs;
  private long fEpochSecs;

  private int fNumOfEvaluatedIndividuals = 0;
  private int fNumOfEvaluatedIndividualsLastProgress;

  // トーナメント選択用の乱数生成器
  private TRandomGenerator fRand;

  public TREAutoMLZero(final int populationSize, final int tournamentSize, final TModel initModel,
      final TEvaluator evaluator, final TAlgorithmMutator mutator, final TRandomGenerator rand,
      final int progressEvery) {
    this("", 0, populationSize, tournamentSize, initModel, evaluator, mutator, rand, progressEvery);
  }

  public TREAutoMLZero(final String logPrefix, final int trailNo, final int populationSize, final int tournamentSize,
      final TModel initModel, final TEvaluator evaluator, final TAlgorithmMutator mutator,
      final TRandomGenerator rand, final int progressEvery) {
    fTrialNo = trailNo;
    fLogPrefix = logPrefix;

    fPopulation = new TPopulation<TIndividual>();
    fPopulationSize = populationSize;

    fTournamentSize = tournamentSize;

    fInitModel = initModel;
    fMutator = mutator;
    fEvaluator = evaluator;

    fProgressEvery = progressEvery;
    fStartSecs = System.currentTimeMillis() / kMillisPerSecond;
    fEpochSecs = fStartSecs;
    fNumOfEvaluatedIndividualsLastProgress = -progressEvery;

    fRand = rand;
  }

  public TPopulation<TIndividual> getPopulation() {
    return fPopulation;
  }

  public long getNumOfTrainSteps() {
    return fEvaluator.getNumOfTrainCompletedSteps();
  }

  public long getNumOfEvaluatedIndividuals() {
    return fNumOfEvaluatedIndividuals;
  }

  public TPopulationStats<TIndividual> getPopulationStats() {
    return fPopulation.getStats();
  }

  private double execute(TAlgorithm algorithm) {
    fNumOfEvaluatedIndividuals++;
    fEpochSecs = System.currentTimeMillis() / kMillisPerSecond;
    double fitness = fEvaluator.execute(algorithm);
    return fitness;
  }

  /**
   * /Initializes the algorithm. Returns the number of individuals evaluated in
   * this call.
   */
  public int initialize() {
    assert !fIsInitialized;
    if (kMutationOutput) {
      try {
        File file = new File(kCSVLogDir + fLogPrefix + "mutation_" + fTrialNo + ".tex");
        fMutationOutputFW = new FileWriter(file);
        fMutationOutputFW.write(TMutationAnalysis.begin(fTrialNo));
      } catch (IOException e) {
        System.out.println(e);
      }
    }
    if (kCSVLogOutput) {
      try {
        File file = new File(kCSVLogDir + fLogPrefix + "eval_" + fTrialNo + ".csv");
        fCSVLogOutputFW = new FileWriter(file);
        fCSVLogOutputFW.write("evaluations_num, elapsed_secs, mean, stdev, best_fit\n");
      } catch (IOException e) {
        System.out.println(e);
      }
    }
    int startNumOfEvaluatedIndividuals = fNumOfEvaluatedIndividuals;
    for (int i = 0; i < fPopulationSize; i++) {
      TAlgorithm algorithm = fInitModel.get();
      double fitness = execute(algorithm);
      TIndividual individual = new TIndividual(algorithm, fitness);
      fPopulation.add(individual);
    }
    assert fPopulation.size() == fPopulationSize;
    printProgress(fPopulation.getStats());
    fIsInitialized = true;
    return fNumOfEvaluatedIndividuals - startNumOfEvaluatedIndividuals;
  }

  private void maybeSaveImprovedMutation(TAlgorithm oldAlgorithm, double oldFitness,
      TAlgorithm newAlgorithm, double newFitness) {
    double diff = newFitness - oldFitness;
    if (diff < 0.05)
      return;
    try {
      fMutationOutputFW
          .write("\\begin{lstlisting}[caption= No. " + getNumOfEvaluatedIndividuals() + "]\n");
      fMutationOutputFW.write("old fitness = " + oldFitness + "\n");
      fMutationOutputFW.write("new fitness = " + newFitness + "\n");
      fMutationOutputFW.write("diff = " + diff + "\n");
      fMutationOutputFW.write("Type:" + fMutator.getActionOfPrevMutation() + "\n");
      if (fMutator.getComponentTypeOfPrevMutation() != null) {
        fMutationOutputFW.write("Target:" + fMutator.getComponentTypeOfPrevMutation());
      }
      if (fMutator.getTargetLine1OfPrevMutation() != null) {
        fMutationOutputFW.write(", line " + fMutator.getTargetLine1OfPrevMutation());
      }
      if (fMutator.getTargetLine2OfPrevMutation() != null) {
        fMutationOutputFW.write(", line " + fMutator.getTargetLine2OfPrevMutation());
      }
      fMutationOutputFW.write("\n\n");
      fMutationOutputFW.write("Before mutation\n");
      fMutationOutputFW.write(oldAlgorithm.toString() + "\n");
      fMutationOutputFW.write("After mutation\n");
      fMutationOutputFW.write(newAlgorithm.toString() + "\n");
      fMutationOutputFW.write("\\end{lstlisting}\n\n\\newpage\n\n");
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  void nextIndividual(final TIndividual individual) {
    assert individual != null;
    TAlgorithm algorithm = individual.getAlgorithm();

    TIndividual baseIndividual = fPopulation.getTournamentSelected(fTournamentSize, fRand);
    double baseFitness = baseIndividual.getFitness();
    TAlgorithm baseAlgorithm = baseIndividual.getAlgorithm();

    algorithm.copyFrom(baseAlgorithm);

    TAlgorithm copyOfBaseAlgorithm = kMutationOutput ? baseAlgorithm.clone() : null;

    TAlgorithm newAlgorithm = fMutator.mutate(algorithm);
    double newFitness = execute(algorithm);

    individual.setFitness(newFitness);

    if (kMutationOutput) {
      maybeSaveImprovedMutation(copyOfBaseAlgorithm, baseFitness, newAlgorithm, newFitness);
    }
  }

  void nextPopulation() {
    fEvaluator.incrementFECGeneration();
    for (TIndividual individual : fPopulation.getAll()) {
      nextIndividual(individual);
    }
  }

  /**
   * Runs for a given amount of time (rounded up to the nearest generation) or for
   * a certain number
   * of train steps (rounded up to the nearest generation), whichever is first.
   * Assumes that Init
   * has been called. Returns the number of train steps executed in this call.
   */
  public long run(final long maxTrainSteps, final long maxMillis, final long maxNumOfEvaluations) {
    assert fIsInitialized;
    long startMillis = System.currentTimeMillis();
    long startTrainSteps = getNumOfTrainSteps();
    long startNumOfEvaluations = getNumOfEvaluatedIndividuals();
    while (getNumOfTrainSteps() - startTrainSteps < maxTrainSteps
        && System.currentTimeMillis() - startMillis < maxMillis
        && getNumOfEvaluatedIndividuals() - startNumOfEvaluations < maxNumOfEvaluations) {
      nextPopulation();
      if (fNumOfEvaluatedIndividuals >= fNumOfEvaluatedIndividualsLastProgress + fProgressEvery) {
        TPopulationStats<TIndividual> stats = fPopulation.getStats();
        printProgress(stats);
        // if (stats.getBestFitness() > 0.9999) {
        // break;
        // }
      }
    }

    if (kMutationOutput) {
      try {
        fMutationOutputFW.write(TMutationAnalysis.end());
        fMutationOutputFW.close();
      } catch (IOException e) {
        System.out.println(e);
      }
    }
    if (kCSVLogOutput) {
      try {
        fCSVLogOutputFW.close();
      } catch (IOException e) {
        System.out.println(e);
      }
    }

    return getNumOfTrainSteps() - startTrainSteps;
  }

  private void printProgress(TPopulationStats<TIndividual> stats) {
    fNumOfEvaluatedIndividualsLastProgress = fNumOfEvaluatedIndividuals;
    long evaluationsNum = fNumOfEvaluatedIndividuals;
    long elapsedSecs = fEpochSecs - fStartSecs;
    String mean = String.format("%05f", stats.getMean());
    String stdev = String.format("%05f", stats.getStdev());
    String bestFit = String.format("%05f", stats.getBestFitness());
    String progress = "";
    progress += "indivs=" + evaluationsNum + ", ";
    progress += "elapsed_secs=" + elapsedSecs + ", ";
    progress += "mean=" + mean + ", ";
    progress += "stdev=" + stdev + ", ";
    progress += "best_fit=" + bestFit + ", ";
    System.out.println(progress);
    if (kCSVLogOutput) {
      try {
        fCSVLogOutputFW.write(evaluationsNum + ", " + elapsedSecs + ", " + mean + ", " + stdev
            + ", " + bestFit + "\n");
      } catch (IOException e) {
        System.out.println(e);
      }

    }
  }
}
