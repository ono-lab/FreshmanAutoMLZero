package methods.MGG_AV;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import core.TAlgorithm;
import core.TAlgorithmValidator;
import core.TEvaluator;
import core.TPopulation;
import core.TPopulationStats;
import file_templates.TElitesAnalysis;
import models.TModel;
import utils.TRandomGenerator;

public class TMGGAutoMLZeroAV {
  private static final long kMillisPerSecond = 1000;
  private static final String kCSVLogDir = "./logs/MGG_AV/";

  // 実験ログのCSV出力
  private static final boolean kCSVLogOutput = true;
  private FileWriter fCSVLogOutputFW;

  // エリート個体の記録
  private static final int kElitesOutputNum = 30;
  private static final int kElitesOutputInterval = 25;
  private static final boolean kElitesOutput = true;
  private FileWriter fElitesOutputFW;

  // バリデーションの結果
  private static final boolean kValidationsOutput = true;
  private FileWriter fValidationsOutputFW;

  private int fTrialNo;
  private String fLogPrefix;

  // Runs up to this many total individuals.
  private int fPopulationSize;
  private TPopulation<TIndividual> fPopulation;

  private int fNumOfChildren;

  // The mutator to use to perform all Elitess.
  private TAlgorithmMutator fMutator;

  private TEvaluator fEvaluator;

  private TAlgorithmValidator fAlgorithmValidator;

  // 初期化用のパラメータ
  private boolean fIsInitialized = false;
  private TModel fInitModel;

  // How frequently to print progress reports.
  private int fProgressEvery;

  private long fStartSecs;
  private long fEpochSecs;

  private int fNumOfEvaluatedIndividuals = 0;
  private int fNumOfEvaluatedIndividualsLastProgress;

  // 世代
  private int fGeneration = 0;

  // 親個体選択用の乱数生成器
  private TRandomGenerator fRand;

  public TMGGAutoMLZeroAV(final int populationSize, final int numOfChildren, final TModel initModel,
      final TEvaluator evaluator, final TAlgorithmMutator mutator, final TRandomGenerator rand,
      final int progressEvery) {
    this("", 0, populationSize, numOfChildren, initModel, evaluator, mutator, rand, progressEvery);
  }

  public TMGGAutoMLZeroAV(final String logPrefix, final int trialNo, final int populationSize, final int numOfChildren,
      final TModel initModel, final TEvaluator evaluator, final TAlgorithmMutator mutator,
      final TRandomGenerator rand, final int progressEvery) {
    this(logPrefix, trialNo, populationSize, numOfChildren, initModel, evaluator, mutator, rand, progressEvery,
        null);
  }

  public TMGGAutoMLZeroAV(final String logPrefix, final int trialNo, final int populationSize, final int numOfChildren,
      final TModel initModel, final TEvaluator evaluator, final TAlgorithmMutator mutator,
      final TRandomGenerator rand, final int progressEvery, TAlgorithmValidator validator) {
    fTrialNo = trialNo;
    fLogPrefix = logPrefix;

    fPopulation = new TPopulation<TIndividual>();
    fPopulationSize = populationSize;

    fNumOfChildren = numOfChildren;

    fInitModel = initModel;
    fMutator = mutator;
    fEvaluator = evaluator;
    fAlgorithmValidator = validator;

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
    if (kElitesOutput) {
      try {
        File file = new File(kCSVLogDir + fLogPrefix + "elites_" + fTrialNo + ".tex");
        fElitesOutputFW = new FileWriter(file);
        fElitesOutputFW.write(TElitesAnalysis.begin(fTrialNo));
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
    if (kValidationsOutput) {
      try {
        File file = new File(kCSVLogDir + fLogPrefix + "validations_" + fTrialNo + ".csv");
        fValidationsOutputFW = new FileWriter(file);
        fValidationsOutputFW.write("generation, success_num, failed_num\n");
      } catch (IOException e) {
        System.out.println(e);
      }
    }
    int startNumOfEvaluatedIndividuals = fNumOfEvaluatedIndividuals;
    for (int i = 0; i < fPopulationSize; i++) {
      TAlgorithm algorithm = fInitModel.get();
      if (fAlgorithmValidator != null) {
        // TODO: バリデーションが通るまでアルゴリズムを生成し続ける
        while (/* 条件式を設定する */) {
          algorithm = fInitModel.get();
        }
      }
      double fitness = execute(algorithm);
      TIndividual individual = new TIndividual(algorithm, fitness);
      fPopulation.add(individual);
    }
    assert fPopulation.size() == fPopulationSize;

    printProgress(fPopulation.getStats());
    if (kElitesOutput) {
      outputElites();
    }
    if (kValidationsOutput)
      outputValidations();
    fIsInitialized = true;
    fGeneration++;
    return fNumOfEvaluatedIndividuals - startNumOfEvaluatedIndividuals;
  }

  private void outputElites() {
    try {
      ArrayList<TIndividual> elites = fPopulation.getElites(kElitesOutputNum);
      for (int eliteIndex = 0; eliteIndex < elites.size(); eliteIndex++) {
        TIndividual individual = elites.get(eliteIndex);
        int rank = eliteIndex + 1;
        fElitesOutputFW
            .write("\\begin{lstlisting}[caption= No. " + fGeneration + "." + rank + "]\n");
        fElitesOutputFW.write("Fitness:" + individual.getFitness() + " \n");
        fElitesOutputFW.write(individual.getAlgorithm().toString() + "\n");
        fElitesOutputFW.write("\\end{lstlisting}\n\n");
        if (eliteIndex % 2 == 1)
          fElitesOutputFW.write("\\newpage\n\n");
      }
      fElitesOutputFW.write("\\newpage\n");
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  private void outputValidations() {
    try {
      fValidationsOutputFW.write(fGeneration + ", " + fAlgorithmValidator.getSuccessCount() + ", "
          + fAlgorithmValidator.getFailedCount() + "\n");
      fAlgorithmValidator.resetCounters();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  void addBest2ChildrenOf(TIndividual parent1, TIndividual parent2) {
    assert fPopulation.size() == fPopulationSize - 2;

    boolean parent1IsHighFitness = parent1.getFitness() < parent2.getFitness();
    TIndividual best1 = parent1IsHighFitness ? parent1.clone() : parent2.clone();
    TIndividual best2 = parent1IsHighFitness ? parent2.clone() : parent1.clone();

    int numOfParent1Children = (int) fNumOfChildren / 2;

    for (int i = 0; i < fNumOfChildren; i++) {
      // バリデーションが通るまでアルゴリズムを生成し続ける
      TAlgorithm parentAlgorithm = i < numOfParent1Children ? new TAlgorithm(parent1.getAlgorithm())
          : new TAlgorithm(parent2.getAlgorithm());
      TAlgorithm childAlgorithm = parentAlgorithm.clone();
      childAlgorithm = fMutator.mutate(childAlgorithm);
      if (fAlgorithmValidator != null) {
        // TODO: バリデーションが通るまでアルゴリズムを生成し続ける
        while (/* 条件式を設定する */) {
          childAlgorithm = parentAlgorithm.clone();
          childAlgorithm = fMutator.mutate(parentAlgorithm);
        }
      }

      double fitness = execute(childAlgorithm);
      if (fitness >= best2.getFitness()) {
        if (fitness >= best1.getFitness()) {
          best2.getAlgorithm().copyFrom(best1.getAlgorithm());
          best2.setFitness(best1.getFitness());
          best1.getAlgorithm().copyFrom(childAlgorithm);
          best1.setFitness(fitness);
        } else {
          best2.getAlgorithm().copyFrom(childAlgorithm);
          best2.setFitness(fitness);
        }
      }
    }

    if (kValidationsOutput)
      outputValidations();

    fPopulation.add(best1);
    fPopulation.add(best2);
    assert fPopulation.size() == fPopulationSize;
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
      fEvaluator.incrementFECGeneration();

      // TODO: 集団からランダムに親個体を2つ取り出す
      // 以下のコードを書く
      // TIndividual parent1 =
      // TIndividual parent2 =

      // TODO: 子個体を生成し評価値の上位2つを集団に追加する
      // ここにコードを書く

      fGeneration++;
      if (fNumOfEvaluatedIndividuals >= fNumOfEvaluatedIndividualsLastProgress + fProgressEvery) {
        TPopulationStats<TIndividual> stats = fPopulation.getStats();
        printProgress(stats);
        if (stats.getBestFitness() > 0.9997) {
          break;
        }
      }
      if (kElitesOutput && fGeneration % kElitesOutputInterval == 0) {
        outputElites();
      }
    }

    if (kElitesOutput) {
      try {
        outputElites();
        fElitesOutputFW.write(TElitesAnalysis.end());
        fElitesOutputFW.close();
      } catch (IOException e) {
        System.out.println(e);
      }
    }

    if (kValidationsOutput) {
      try {
        fValidationsOutputFW.close();
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

  private double printProgress(TPopulationStats<TIndividual> stats) {
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
    return stats.getBestFitness();
  }
}
