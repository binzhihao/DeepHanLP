package com.hankcs.nlp;

import com.hankcs.nlp.extractor.TextRankExtractor;
import com.hankcs.nlp.extractor.base.AbstractExtractor;
import com.hankcs.nlp.segment.NotionalSegment;
import com.hankcs.nlp.segment.ViterbiSegment;
import com.hankcs.nlp.segment.base.AbstractSegment;
import com.hankcs.nlp.semantic.word2vec.Word2VecModel;
import com.hankcs.nlp.semantic.word2vec.Word2VecTrainer;
import com.hankcs.nlp.semantic.word2vec.base.AbstractTrainer;
import com.hankcs.nlp.semantic.word2vec.base.AbstractVectorModel;

import java.io.IOException;

/**
 * 主程序的唯一入口
 */
public class NLPManager {

    public static final int SEG_VITERBI = 1;    //常规分词，分词结果包含停用词和标点符号等，可用于基准测试
    public static final int SEG_NGRAM = 2;      //高阶分词，二阶隐马
    public static final int SEG_NOTION = 3;     //常规分词，去掉停用词

    public static final int EXT_IDP = 10;
    public static final int EXT_TEXTRANK = 20;

    private static class SingletonHolder {
        private static final NLPManager INSTANCE = new NLPManager();
    }

    public static NLPManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private NLPManager() {
        //加载词典并缓存 2018.02.28 惰性加载，不再在这里做处理
    }

    /**
     * 获取默认的分词器
     *
     * @return default
     */
    public AbstractSegment getSegment() {
        return getSegment(SEG_VITERBI);
    }

    public AbstractSegment getSegment(int type) {
        AbstractSegment segment = null;
        switch (type) {
            case SEG_VITERBI:
                segment = new ViterbiSegment();
                break;
            case SEG_NGRAM:
                // TODO: 2018/2/5
                break;
            case SEG_NOTION:
                segment = new NotionalSegment();
                break;
            default:
                break;
        }
        return segment;
    }

    /**
     * 获取默认的关键词提取器
     */
    public AbstractExtractor getExtractor() {
        return getExtractor(EXT_TEXTRANK);
    }

    public AbstractExtractor getExtractor(int type) {
        AbstractExtractor keywordExtractor = null;
        switch (type) {
            case EXT_IDP:
                break;
            case EXT_TEXTRANK:
                keywordExtractor = new TextRankExtractor();
                break;
            default:
                break;
        }
        return keywordExtractor;
    }

    /**
     * 获取训练器
     *
     * @return
     */
    public AbstractTrainer getTrainer() {
        return new Word2VecTrainer();
    }

    public AbstractVectorModel<String> getWordVectorModel(String modelPath) throws IOException {
        return new Word2VecModel(modelPath);
    }

}
