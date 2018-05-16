package com.hankcs.nlp.model.maxent;

/**
 * 封装了模型用来计算概率的一些参数
 *
 * @author open-nlp
 */
public class EvalParameters {

    /**
     * 将输出与参数映射起来，下标可以用 <code>pmap</code> 查询到
     */
    private Context[] params;
    /**
     * 一共有几种输出
     */
    private final int numOutcomes;
    /**
     * 一个事件中最多包含的特征数
     */
    private double correctionConstant;

    /**
     * correctionConstant的倒数
     */
    private final double constantInverse;
    /**
     * 修正参数
     */
    private double correctionParam;

    /**
     * 创建一个参数，可被用于预测
     *
     * @param params             环境
     * @param correctionParam    修正参数
     * @param correctionConstant 一个事件中最多包含的特征数
     * @param numOutcomes        事件的可能label数
     */
    public EvalParameters(Context[] params, double correctionParam, double correctionConstant, int numOutcomes) {
        this.params = params;
        this.correctionParam = correctionParam;
        this.numOutcomes = numOutcomes;
        this.correctionConstant = correctionConstant;
        this.constantInverse = 1.0 / correctionConstant;
    }

    public EvalParameters(Context[] params, int numOutcomes) {
        this(params, 0, 0, numOutcomes);
    }

    public Context[] getParams() {
        return params;
    }

    public int getNumOutcomes() {
        return numOutcomes;
    }

    public double getCorrectionConstant() {
        return correctionConstant;
    }

    public double getConstantInverse() {
        return constantInverse;
    }

    public double getCorrectionParam() {
        return correctionParam;
    }

    public void setCorrectionParam(double correctionParam) {
        this.correctionParam = correctionParam;
    }
}