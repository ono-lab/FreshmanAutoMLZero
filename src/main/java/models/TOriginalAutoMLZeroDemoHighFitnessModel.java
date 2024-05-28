package models;

import java.util.ArrayList;
import core.TAlgorithm;
import core.TComponentType;
import core.TInstruction;
import core.TOp;
import core.instruction_data_setter.TActivationDataSetter;

/**
 * 移行前のC++のAutoMLZeroのdemoを実行した結果出てきたアルゴリズム
 */
public class TOriginalAutoMLZeroDemoHighFitnessModel extends TModel {
  public TOriginalAutoMLZeroDemoHighFitnessModel() {}

  @Override
  protected TAlgorithm getImpl() {
    TAlgorithm algorithm = new TAlgorithm();
    ArrayList<TInstruction> setup = algorithm.getComponent(TComponentType.SETUP);
    setup.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 3, 1, 1));
    setup.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 3, 2, 1));
    setup.add(new TInstruction(TOp.VECTOR_SUM_OP, 2, 1, 2));
    setup.add(new TInstruction(TOp.SCALAR_DIFF_OP, 3, 3, 2));
    setup.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, 1, new TActivationDataSetter(0.0951716)));
    setup.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 3, 3, 3));
    setup.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, 2, new TActivationDataSetter(-0.166948)));
    setup.add(new TInstruction(TOp.VECTOR_SUM_OP, 0, 0, 2));
    setup.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 0, 1, 1));

    ArrayList<TInstruction> predict = algorithm.getComponent(TComponentType.PREDICT);
    predict.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 3, 0, 3));
    predict.add(new TInstruction(TOp.VECTOR_INNER_PRODUCT_OP, 2, 0, 1));

    ArrayList<TInstruction> learn = algorithm.getComponent(TComponentType.LEARN);
    learn.add(new TInstruction(TOp.SCALAR_DIFF_OP, 1, 0, 3));
    learn.add(new TInstruction(TOp.VECTOR_SUM_OP, 1, 2, 2));
    learn.add(new TInstruction(TOp.SCALAR_VECTOR_PRODUCT_OP, 2, 0, 1));
    learn.add(new TInstruction(TOp.SCALAR_PRODUCT_OP, 3, 3, 2));
    learn.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, 2, new TActivationDataSetter(0.528608)));
    learn.add(new TInstruction(TOp.SCALAR_VECTOR_PRODUCT_OP, 3, 1, 1));
    learn.add(new TInstruction(TOp.SCALAR_CONST_SET_OP, 2, new TActivationDataSetter(-0.0702741)));
    learn.add(new TInstruction(TOp.VECTOR_SUM_OP, 2, 1, 2));
    return algorithm;
  }
}
