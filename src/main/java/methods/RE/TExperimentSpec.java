package methods.RE;

import core.TFECSpec;
import core.TFitnessCombinationMode;
import core.TOp;
import core.TTrainBudgetSpec;
import models.TModel;
import task_factory.spec.TTaskSpec;

/**
 * Stores the entire configuration of an experiment.
 */
class TExperimentSpec {
  // Tasks for T_search.
  public long searchTasksSeed = 0;
  public TTaskSpec[] searchTaskSpecs;

  // Tasks for T_select.
  public long selectTasksSeed = 100;
  public TTaskSpec[] selectTaskSpecs;

  // Tasks for T_final.
  public TTaskSpec[] finalTaskSpecs;

  // 乱数生成器
  public long searchSeed = 20;
  public long selectSeed = 40;
  public long finalSeed = 60;

  // メモリーのアドレスの上限
  public int numOfScalarAddresses;
  public int numOfVectorAddresses;
  public int numOfMatrixAddresses;

  // Allowed ops in each of the component functions. To prevent evolving one
  // component function, leave the corresponding list of ops as empty.
  public TOp[] setupOps;
  public TOp[] predictOps;
  public TOp[] learnOps;

  // 集団サイズ
  public int populationSize;

  // トーナメントサイズ
  public int tournamentSize;

  // 初期化用のモデル
  // The algorithm to use to initialize the population. Typically, a
  // NO_OP_ALGORITHM or a
  // RANDOM_ALGORITHM.
  public boolean sameInitModel = false;
  public TModel initModel;

  // 突然変異をする確率
  // Probability that a child will be mutated from the parent. If not mutated,
  // the child is identical to the parent.
  public double mutateProb = 0.9;

  // 突然変異の種類
  // The mutation types that can happen during the experiment.
  public TAlgorithmMutation[] allowedMutations;

  // The minimum and maximum allowed number of instructions for the component
  // functions. These are required only if insert/remove mutations are used
  // (otherwise, the number of instructions remains at the setup_size_init,
  // predict_size_init, and learn_size_init values).
  public int mutateSetupSizeMin;
  public int mutateSetupSizeMax;
  public int mutatePredictSizeMin;
  public int mutatePredictSizeMax;
  public int mutateLearnSizeMin;
  public int mutateLearnSizeMax;

  // If not present, cache is disabled.
  public TFECSpec FECSpec;

  // タスク集合内の各タスクのFitnessから全体のFitnessを作る時に使う方法
  public TFitnessCombinationMode fitnessCombinationMode = TFitnessCombinationMode.MEAN;

  // Maximum absolute error. If an algorithm produces errors larger
  // than this after any example during in its training or validation, it "
  // will be assigned the minimum fitness if early stopping is used.
  public double maxAbsError = 100.0;

  // 探索を終了にするのに十分なFitness
  // 0の場合は指定せず上限の回数まで処理を行う
  public double sufficientFitness = 0.0;

  // Training budget. If omitted, no training budget is imposed.
  public TTrainBudgetSpec trainBudgetSpec;

  // Total number of train steps executed in the experiment. The default yields
  // ~1000000 individuals each trained for 8000 steps on 10 tasks, when
  // techniques that reduce train steps per individual, such as FEC and hurdles,
  // are not used. Note, "~1000000 individuals" is approximate because of
  // early stopping; however, in practice, we did not witness early stopping
  // drastically reduce the number of individuals.
  public long maxTrainSteps = Long.MAX_VALUE;

  // 評価回数の上限
  public long maxNumOfEvaluations = Long.MAX_VALUE;

  // The period between progress reports to stdout, as the experiment
  // advances.
  public int progressEvery = 10000;

  public boolean randomizeTaskSeeds;

  // 実験回数の最大値
  // 0の場合は指定せず上限の回数まで処理を行う
  public int maxNumOfExperiments = 0;
  public int initialNumOfExperiments = 0;

  // 乱数シード調整要
  public int seedOffset = 0;

  public String logFilePrefix = "";
}
