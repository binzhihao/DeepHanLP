package com.hankcs.nlp.semantic.word2vec;


import com.hankcs.nlp.semantic.word2vec.base.AbstractTrainer;
import com.hankcs.nlp.semantic.word2vec.base.Config;
import com.hankcs.nlp.semantic.word2vec.base.NeuralNetworkType;
import com.hankcs.nlp.semantic.word2vec.callback.ITrainingCallback;
import com.hankcs.nlp.semantic.word2vec.util.Preconditions;
import com.hankcs.nlp.semantic.word2vec.util.CommonUtils;
import com.hankcs.nlp.util.TextUtility;

import java.io.IOException;

import static com.hankcs.nlp.util.LogUtil.logger;

/**
 * 词向量训练工具
 */
public class Word2VecTrainer extends AbstractTrainer {

    private Integer layerSize = 100;    //词向量维数
    private Integer windowSize = 5;
    private Integer numThreads = Runtime.getRuntime().availableProcessors();
    private int negativeSamples = 25;
    private boolean useHierarchicalSoftmax;
    private Integer minFrequency = 5;
    private float downSampleRate = 0.0001f;
    private Integer iterations = 15;    //迭代次数
    private NeuralNetworkType type = NeuralNetworkType.SKIP_GRAM;
    private ITrainingCallback callback;

    public Word2VecTrainer() {
    }

    @Override
    public Word2VecTrainer setCallback(ITrainingCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public Word2VecTrainer setLayerSize(int layerSize) {
        Preconditions.checkArgument(layerSize > 0, "Value must be positive");
        this.layerSize = layerSize;
        return this;
    }

    @Override
    public Word2VecTrainer setWindowSize(int windowSize) {
        Preconditions.checkArgument(windowSize > 0, "Value must be positive");
        this.windowSize = windowSize;
        return this;
    }

    @Override
    public Word2VecTrainer setType(NeuralNetworkType type) {
        this.type = Preconditions.checkNotNull(type);
        return this;
    }

    @Override
    public Word2VecTrainer enableHierarchicalSoftmax() {
        this.useHierarchicalSoftmax = true;
        return this;
    }

    @Override
    public Word2VecTrainer setNegativeSamples(int negativeSamples) {
        Preconditions.checkArgument(negativeSamples >= 0, "Value must be non-negative");
        this.negativeSamples = negativeSamples;
        return this;
    }

    @Override
    public Word2VecTrainer setMinVocabFrequency(int minFrequency) {
        Preconditions.checkArgument(minFrequency >= 0, "Value must be non-negative");
        this.minFrequency = minFrequency;
        return this;
    }

    @Override
    public Word2VecTrainer setDownSamplingRate(float downSampleRate) {
        Preconditions.checkArgument(downSampleRate >= 0, "Value must be non-negative");
        this.downSampleRate = downSampleRate;
        return this;
    }

    @Override
    public Word2VecTrainer setNumIterations(int iterations) {
        Preconditions.checkArgument(iterations > 0, "Value must be positive");
        this.iterations = iterations;
        return this;
    }


    /**
     * 执行训练
     *
     * @param trainFileName 输入语料文件(已分词)
     * @param modelFileName 输出模型路径
     * @return 词向量模型，训练完成之后就可以用这个向量模型来进行一些操作了
     */
    public Word2VecModel train(String trainFileName, String modelFileName) {
        Config config = new Config();
        config.setInputFile(trainFileName);
        config.setLayer1Size(layerSize);
        config.setUseContinuousBagOfWords(type == NeuralNetworkType.CBOW);
        config.setUseHierarchicalSoftmax(useHierarchicalSoftmax);
        config.setNegative(negativeSamples);
        config.setNumThreads(numThreads);
        config.setAlpha(type.getDefaultInitialLearningRate());  //采用默认的学习率
        config.setSample(downSampleRate);
        config.setWindow(windowSize);
        config.setIter(iterations);
        config.setMinCount(minFrequency);
        config.setOutputFile(modelFileName);
        config.setCallback(callback);
        Word2VecTraining model = new Word2VecTraining(config);

        try {
            long timeStart = System.currentTimeMillis();
            model.trainModel();
            System.out.println();
            System.out.printf("训练结束，一共耗时：%s\n", CommonUtils.humanTime(System.currentTimeMillis() - timeStart));
            return new Word2VecModel(modelFileName);
        } catch (IOException e) {
            logger.warning("训练过程中发生IO异常\n" + TextUtility.exceptionToString(e));
        }

        return null;
    }
}