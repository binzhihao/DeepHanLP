package com.hankcs.nlp.model.maxent;

/**
 * 先验概率计算工具， 后续统一放到数学工具去
 */
public class UniformPrior {
    private int numOutcomes;
    private double r;

    /**
     * 获取先验概率
     *
     * @param dist 储存位置
     */
    public void logPrior(double[] dist) {
        for (int oi = 0; oi < numOutcomes; oi++) {
            dist[oi] = r;
        }
    }

    /**
     * 初始化
     *
     * @param outcomeLabels
     */
    public void setLabels(String[] outcomeLabels) {
        this.numOutcomes = outcomeLabels.length;
        r = Math.log(1.0 / numOutcomes);
    }
}
