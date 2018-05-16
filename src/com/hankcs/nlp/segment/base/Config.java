package com.hankcs.nlp.segment.base;

/**
 * 分词器配置项
 */
public class Config {
    /**
     * 是否识别中国人名
     */
    public boolean nameRecognize = true;
    /**
     * 是否识别音译人名
     */
    public boolean translatedNameRecognize = true;
    /**
     * 是否识别日本人名
     */
    public boolean japaneseNameRecognize = false;
    /**
     * 是否识别地名
     */
    public boolean placeRecognize = false;
    /**
     * 是否识别机构
     */
    public boolean organizationRecognize = false;
    /**
     * 是否加载用户词典
     */
    public boolean useCustomDictionary = true;
    /**
     * 用户词典高优先级
     */
    public boolean forceCustomDictionary = false;
    /**
     * 词性标注
     */
    public boolean speechTagging = false;
    /**
     * 命名实体识别
     */
    public boolean ner = true;
    /**
     * 是否计算偏移量
     */
    public boolean offset = false;
    /**
     * 并行分词的线程数，时间仓促，没有严格意义上地多开
     */
    public int threadNumber = 1;
}
