package com.hankcs.nlp.semantic.word2vec.callback;

/**
 * 用于词向量训练过程中的回调显示
 */
public interface ITrainingCallback {

    /**
     * 语料加载完毕
     *
     * @param vocWords   词表大小（不是词频，而是语料中有多少种词）
     * @param trainWords 实际训练用到的词的总词频（有些词被停用词过滤掉）
     * @param totalWords 全部词语的总词频
     */
    void corpusLoaded(int vocWords, int trainWords, int totalWords);

    /**
     * 训练过程的回调
     *
     * @param alpha    学习率
     * @param progress 训练完成百分比（0-100）
     */
    void training(float alpha, float progress);
}
