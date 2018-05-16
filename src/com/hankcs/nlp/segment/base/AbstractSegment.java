package com.hankcs.nlp.segment.base;

import com.hankcs.nlp.segment.util.NormalizationUtil;
import com.hankcs.nlp.segment.common.Term;

import java.util.List;

public abstract class AbstractSegment {

    //分词器的配置
    protected Config config;

    public AbstractSegment() {
        config = new Config();
    }

    /**
     * 分词
     *
     * @param text 待分词文本
     * @return 单词列表
     */
    public List<Term> seg(char[] text) {
        assert text != null;
        char[] realText;
        if (config.Normalization) {
            realText = NormalizationUtil.getInstance().normalize(text);
        } else {
            realText = text;
        }
        return segSentence(realText);
    }

    /**
     * 不同的算法实现这个方法
     *
     */
    protected abstract List<Term> segSentence(char[] text);

    public static class Config {

        public String HMMSegmentModelPath = null;

        public boolean ShowTermNature = false;

        public boolean Normalization = false;

        public boolean forceCustomDictionary = true;

        public boolean useCustomDictionary = true;

        public boolean indexMode = false;

        // 是否识别数字和量词
        public boolean numberQuantifierRecognize = false;

        // 命名实体识别是否至少有一项被激活
        public boolean ner = true;

        public boolean nameRecognize = true;

        public boolean translatedNameRecognize = true;

        public boolean placeRecognize = true;

        public boolean speechTagging = false;
    }
}
