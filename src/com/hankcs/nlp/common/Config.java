package com.hankcs.nlp.common;

import com.hankcs.nlp.dependence.io.IIOAdapter;

/**
 * 全局配置
 */
public final class Config {

    /**
     * 控制主程序的Log输出
     */
    public static boolean APP_DEBUG = false;
    /**
     * 开发模式，控制一些逻辑以及库程序的Log输出
     */
    public static boolean DEBUG = false;
    /**
     * 核心词典路径
     */
    public static String CoreDictionaryPath = "data/dictionary/core/CoreNatureDictionary.txt";
    /**
     * 核心词典词性转移矩阵路径
     */
    public static String CoreDictionaryTransformMatrixDictionaryPath = "data/dictionary/core/CoreNatureDictionary.tr.txt";
    /**
     * 用户自定义词典路径，自定义词典可能有很多，论文建议将自定义词典按照词性或者领域拆分成多个文件，方便管理和更新
     */
    public static String CustomDictionaryPath[] = new String[]{"data/dictionary/custom/CustomDictionary.txt"};
    /**
     * 2元语法词典路径
     */
    public static String BiGramDictionaryPath = "data/dictionary/core/CoreNatureDictionary.ngram.txt";
    /**
     * 停用词词典路径
     */
    public static String CoreStopWordDictionaryPath = "data/dictionary/core/CoreStopWords.txt";
    /**
     * 人名词典路径
     */
    public static String PersonDictionaryPath = "data/dictionary/person/nr.txt";
    /**
     * 人名词典转移矩阵路径
     */
    public static String PersonDictionaryTrPath = "data/dictionary/person/nr.tr.txt";
    /**
     * 地名词典路径
     */
    public static String PlaceDictionaryPath = "data/dictionary/place/ns.txt";
    /**
     * 地名词典转移矩阵路径
     */
    public static String PlaceDictionaryTrPath = "data/dictionary/place/ns.tr.txt";
    /**
     * 机构名词典路径
     */
    public static String OrganizationDictionaryPath = "data/dictionary/organization/nt.txt";
    /**
     * 机构名词典转移矩阵路径
     */
    public static String OrganizationDictionaryTrPath = "data/dictionary/organization/nt.tr.txt";
    /**
     * 拼音词典路径
     */
    public static String PinyinDictionaryPath = "data/dictionary/pinyin/pinyin.txt";
    /**
     * 音译人名词典
     */
    public static String TranslatedPersonDictionaryPath = "data/dictionary/person/nrf.txt";
    /**
     * 日本人名词典路径
     */
    public static String JapanesePersonDictionaryPath = "data/dictionary/person/nrj.txt";
    /**
     * 字符类型对应表
     */
    public static String CharTypePath = "data/dictionary/other/CharType.bin";
    /**
     * 字符正规化表（全角转半角，繁体转简体）
     */
    public static String CharTablePath = "data/dictionary/other/CharTable.txt";
    /**
     * 2阶HMM分词模型
     */
    public static String HMMSegmentModelPath = "data/model/segment/HMMSegmentModel.bin";
    /**
     * 分词结果是否展示词性
     */
    public static boolean ShowTermNature = true;
    /**
     * 是否执行字符正规化（繁体->简体，全角->半角，大写->小写），切换配置后必须删CustomDictionary.txt.bin缓存重新生成
     */
    public static boolean Normalization = false;

    public static IIOAdapter IOAdapter;
}
