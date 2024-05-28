package methods.MGG_AV;

import core.TAlgorithm;
import core.TAllowedOpsManager;
import core.TEvaluator;
import core.TFECCache;
import core.TFitnessCombinationMode;
import core.TMemory;
import core.TPopulationStats;
import core.TTrainBudget;
import task_factory.TTaskFactory;
import utils.TRandomGenerator;

class TExperimentRunner {
  // Useful constant to represent an "infinity" but is only about ~1000x
  // the largest value we would use (to prevent numeric overflows).
  private static final long kUnlimitedTimeMillis = Long.MAX_VALUE;

  private static final long kMutatorSeedOffset = 100;
  private static final long kEvaluatorSeedOffset = 200;
  private static final long kAutoMLZeroMGGSeedOffset = 300;

  static void execute(TExperimentSpec spec) {
    // メモリの設定
    TMemory.numOfScalarAddresses = spec.numOfScalarAddresses;
    TMemory.numOfVectorAddresses = spec.numOfVectorAddresses;
    TMemory.numOfMatrixAddresses = spec.numOfMatrixAddresses;

    // Build reusable search and select structures.
    final TTrainBudget trainBudget = spec.trainBudgetSpec != null ? new TTrainBudget(spec.trainBudgetSpec) : null;

    final TAllowedOpsManager allowedOpsManager = new TAllowedOpsManager(spec.setupOps, spec.predictOps, spec.learnOps);

    // Run search experiments and select best algorithm.
    int numOfExperiments = spec.initialNumOfExperiments;
    assert spec.initialNumOfExperiments <= spec.maxNumOfExperiments;

    TAlgorithm bestAlgorithm = new TAlgorithm();
    double bestSelectFitness = Double.MIN_VALUE;
    double[] selectFitnesses = new double[spec.maxNumOfExperiments - spec.initialNumOfExperiments + 1];

    while (true) {
      final TAlgorithmMutator mutator = new TAlgorithmMutator(spec.allowedMutations,
          spec.mutateProb, spec.mutateSetupSizeMin, spec.mutateSetupSizeMax,
          spec.mutatePredictSizeMin, spec.mutatePredictSizeMax, spec.mutateLearnSizeMin,
          spec.mutateLearnSizeMax, allowedOpsManager, new TRandomGenerator(
              spec.searchSeed + kMutatorSeedOffset + spec.seedOffset + numOfExperiments));

      // Randomize T_search tasks.
      if (spec.randomizeTaskSeeds) {
        TTaskFactory.randomizeSeeds(spec.searchTaskSpecs,
            spec.searchTasksSeed + spec.seedOffset + numOfExperiments);
      } else {
        TTaskFactory.randomizeSeeds(spec.searchTaskSpecs, spec.seedOffset + spec.searchTasksSeed);
      }

      // Build non-reusable search structures.
      final TFECCache FECCache = spec.FECSpec != null ? new TFECCache(spec.FECSpec) : null;
      final TEvaluator evaluator = new TEvaluator(spec.fitnessCombinationMode, spec.searchTaskSpecs,
          FECCache, trainBudget, spec.maxAbsError, new TRandomGenerator(
              kEvaluatorSeedOffset + spec.searchSeed + spec.seedOffset + numOfExperiments));
      spec.initModel
          .initialize(spec.sameInitModel ? spec.seedOffset : spec.seedOffset + numOfExperiments);
      final TMGGAutoMLZeroAV autoMLZeroMGG = new TMGGAutoMLZeroAV(spec.logFilePrefix, numOfExperiments,
          spec.populationSize,
          spec.numOfChildren, spec.initModel, evaluator, mutator,
          new TRandomGenerator(
              spec.searchSeed + kAutoMLZeroMGGSeedOffset + spec.seedOffset + numOfExperiments),
          spec.progressEvery, spec.validator);

      // Run one experiment.
      System.out.println("Experiment " + numOfExperiments);
      System.out.println("Running evolution experiment (on the T_search tasks)...");
      autoMLZeroMGG.initialize();
      final long remainingTrainSteps = spec.maxTrainSteps - autoMLZeroMGG.getNumOfTrainSteps();
      final long remainingNumOfEvaluations = spec.maxNumOfEvaluations - autoMLZeroMGG.getNumOfEvaluatedIndividuals();
      autoMLZeroMGG.run(remainingTrainSteps, kUnlimitedTimeMillis, remainingNumOfEvaluations);
      System.out.println("Experiment done. Retrieving candidate algorithm.");

      // Extract best algorithm based on T_search.
      final TPopulationStats<TIndividual> stats = autoMLZeroMGG.getPopulationStats();
      final TAlgorithm candidateAlgorithm = stats.getBestAlgorithm();
      System.out
          .println("Search fitness for candidate algorithm = " + stats.getBestFitness() + ".");

      // Randomize T_select tasks.
      if (spec.randomizeTaskSeeds) {
        TTaskFactory.randomizeSeeds(spec.selectTaskSpecs,
            spec.selectTasksSeed + spec.seedOffset + numOfExperiments);
      } else {
        TTaskFactory.randomizeSeeds(spec.selectTaskSpecs, spec.seedOffset + spec.selectTasksSeed);
      }
      // Keep track of the best model on the T_select tasks.
      System.out.println("Evaluating candidate algorithm from experiment (on T_select tasks)...");
      final TEvaluator selectEvaluator = new TEvaluator(TFitnessCombinationMode.MEAN,
          spec.selectTaskSpecs, null, null, spec.maxAbsError, new TRandomGenerator(
              spec.searchSeed + kEvaluatorSeedOffset + spec.seedOffset + numOfExperiments));
      final double selectFitness = selectEvaluator.execute(candidateAlgorithm);
      System.out.println("Select fitness for candidate algorithm = " + selectFitness + ".");
      selectFitnesses[numOfExperiments - spec.initialNumOfExperiments] = selectFitness;
      if (selectFitness >= bestSelectFitness) {
        bestSelectFitness = selectFitness;
        bestAlgorithm = candidateAlgorithm;
        System.out.println("Select fitness is the best so far.");
      }

      // Consider stopping experiments.
      if (spec.sufficientFitness > 0.0 && bestSelectFitness > spec.sufficientFitness) {
        // Stop if we reached the specified `sufficientFitness`.
        break;
      }
      if (spec.maxNumOfExperiments > 0 && numOfExperiments >= spec.maxNumOfExperiments) {
        // Stop if we reached the maximum number of experiments.
        break;
      }
      numOfExperiments++;
    }
    for (int i = spec.initialNumOfExperiments; i <= spec.maxNumOfExperiments; i++) {
      System.out.println(
          "Select fitness (" + i + "): " + selectFitnesses[i - spec.initialNumOfExperiments]);
    }
    // Do a final evaluation on unseen tasks.
    System.out.println("Final evaluation of best algorithm (on unseen tasks)...");
    final TEvaluator finalEvaluator = new TEvaluator(TFitnessCombinationMode.MEAN, spec.finalTaskSpecs, null, null,
        spec.maxAbsError, new TRandomGenerator(spec.finalSeed + kEvaluatorSeedOffset));
    final double finalFitness = finalEvaluator.execute(bestAlgorithm);
    System.out.println("Final evaluation fitness (on unseen data) = " + finalFitness + ".");
    System.out.println("Algorithm found:");
    System.out.println(bestAlgorithm);
  }
}
