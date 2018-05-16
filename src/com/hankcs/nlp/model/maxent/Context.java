package com.hankcs.nlp.model.maxent;

/**
 * 将参数与特征关联起来的类，用来储存最大熵的参数，也用来储存模型和经验分布
 */
public class Context {

    /**
     * 参数
     */
    protected double[] parameters;
    /**
     * 输出（标签）
     */
    protected int[] outcomes;

    /**
     * 构建一个新的上下文
     *
     * @param outcomePattern 输出
     * @param parameters     参数
     */
    public Context(int[] outcomePattern, double[] parameters) {
        this.outcomes = outcomePattern;
        this.parameters = parameters;
    }

    /**
     * 获取输出
     *
     * @return 输出数组
     */
    public int[] getOutcomes() {
        return outcomes;
    }

    /**
     * 获取参数
     *
     * @return 参数数组
     */
    public double[] getParameters() {
        return parameters;
    }
}
