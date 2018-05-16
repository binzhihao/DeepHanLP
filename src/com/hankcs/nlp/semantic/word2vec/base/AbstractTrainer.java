package com.hankcs.nlp.semantic.word2vec.base;

import com.hankcs.nlp.semantic.word2vec.Word2VecModel;
import com.hankcs.nlp.semantic.word2vec.callback.ITrainingCallback;

public abstract class AbstractTrainer {
    /**
     * 设置训练回调
     *
     * @param callback 回调接口
     */
    public abstract AbstractTrainer setCallback(ITrainingCallback callback);

    /**
     * 神经网络类型
     *
     * @see {@link NeuralNetworkType}
     *
     * 默认 SKIP_GRAM
     */
    public abstract AbstractTrainer setType(NeuralNetworkType type);

    /**
     * 启用 hierarchical softmax
     *
     * 默认关闭
     */
    public abstract AbstractTrainer enableHierarchicalSoftmax();

    /**
     * 负采样样本数
     *
     * 一般在 5 到 10 之间, 默认 0
     */
    public abstract AbstractTrainer setNegativeSamples(int negativeSamples);

    /**
     * 词向量的维度（等同于神经网络模型隐藏层的大小）
     *
     * 默认 100
     */
    public abstract AbstractTrainer setLayerSize(int layerSize);

    /**
     * 窗口大小
     *
     * 默认 5
     */
    public abstract AbstractTrainer setWindowSize(int windowSize);

    /**
     * 最低词频，低于此数值将被过滤掉
     *
     * 默认 5
     */
    public abstract AbstractTrainer setMinVocabFrequency(int minFrequency);

    /**
     * 设置高频词的下采样频率（高频词频率一旦高于此频率，训练时将被随机忽略），在不使用停用词词典的情况下，停用词就符合高频词的标准
     *
     * 默认 1e-3, 常用取值区间为 (0, 1e-5)
     */
    public abstract AbstractTrainer setDownSamplingRate(float downSampleRate);

    /**
     * 设置迭代次数
     */
    public abstract AbstractTrainer setNumIterations(int iterations);

    public abstract Word2VecModel train(String trainFileName, String modelFileName);
}
