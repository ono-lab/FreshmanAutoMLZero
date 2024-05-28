package task_factory;

import java.util.ArrayList;
import core.TTask;
import core.TTaskBuffer;
import task_factory.spec.*;
import utils.THashGenerator;
import utils.TRandomGenerator;

public class TTaskFactory {

  // The values of the seeds below.
  private static final long[] kDefaultFirstParamSeeds =
      new long[] {1001, 1012, 1010, 1000, 1006, 1008, 1007, 1003};

  private static final long[] kDefaultFirstDataSeeds =
      new long[] {11001, 11012, 11010, 11000, 11006, 11008, 11007, 11003};

  static private TTask createOneTask(int taskIndex, long paramSeed, long dataSeed, TTaskSpec spec) {
    TTaskBuffer buffer = spec.createOneTaskBuffer(paramSeed, dataSeed);
    TRandomGenerator rand = new TRandomGenerator(dataSeed + 3274582109L);
    assert buffer.getTrainDataSize() == spec.numOfTrainExamples;
    assert buffer.getValidDataSize() == spec.numOfValidExamples;
    return new TTask(taskIndex, spec.numOfTrainEpochs, buffer, rand);
  }

  static private void addTasksByOneSpec(ArrayList<TTask> tasks, TTaskSpec spec) {
    int numOfTasks = spec.numOfTasks;
    assert numOfTasks > 0;

    long[] firstParamSeeds = spec.paramSeeds != null && spec.paramSeeds.length > 0 ? spec.paramSeeds
        : kDefaultFirstParamSeeds;
    long[] firstDataSeeds = spec.dataSeeds != null && spec.dataSeeds.length > 0 ? spec.dataSeeds
        : kDefaultFirstDataSeeds;

    long paramSeed = firstParamSeeds[0], dataSeed = firstDataSeeds[0];
    for (int i = 0; i < numOfTasks; i++) {
      paramSeed = i < firstParamSeeds.length ? firstParamSeeds[i] : paramSeed + 1;
      dataSeed = i < firstDataSeeds.length ? firstDataSeeds[i] : dataSeed + 1;
      int taskIndex = tasks.size();
      TTask task = createOneTask(taskIndex, paramSeed, dataSeed, spec);
      tasks.add(task);
    }
  }

  static public void fillTasks(ArrayList<TTask> tasks, TTaskSpec[] specs) {
    if (tasks.size() != 0) {
      tasks.clear();
    }
    for (TTaskSpec spec : specs) {
      addTasksByOneSpec(tasks, spec);
    }
  }

  static public void fillTasks(ArrayList<TTask> tasks, TTaskSpec spec) {
    if (tasks.size() != 0) {
      tasks.clear();
    }
    addTasksByOneSpec(tasks, spec);
  }

  static public ArrayList<TTask> createTasks(TTaskSpec[] spec) {
    ArrayList<TTask> tasks = new ArrayList<TTask>();
    fillTasks(tasks, spec);
    return tasks;
  }

  static public ArrayList<TTask> createTasks(TTaskSpec spec) {
    ArrayList<TTask> tasks = new ArrayList<TTask>();
    fillTasks(tasks, spec);
    return tasks;
  }

  static public void randomizeSeeds(TTaskSpec[] specs, final long seed) {
    long baseParamSeed = THashGenerator.getCombinedHash(seed, 85652777L);
    long baseDataSeed = THashGenerator.getCombinedHash(seed, 38272328L);
    TRandomGenerator paramSeedGen = new TRandomGenerator(baseParamSeed);
    TRandomGenerator dataSeedGen = new TRandomGenerator(baseDataSeed);
    for (TTaskSpec spec : specs) {
      if (spec.paramSeeds == null || spec.paramSeeds.length != spec.numOfTasks) {
        spec.paramSeeds = new long[spec.numOfTasks];
      }
      if (spec.dataSeeds == null || spec.dataSeeds.length != spec.numOfTasks) {
        spec.dataSeeds = new long[spec.numOfTasks];
      }
      for (int i = 0; i < spec.numOfTasks; i++) {
        spec.paramSeeds[i] = paramSeedGen.nextLong();
        spec.dataSeeds[i] = dataSeedGen.nextLong();
      }
    }
  }
}
