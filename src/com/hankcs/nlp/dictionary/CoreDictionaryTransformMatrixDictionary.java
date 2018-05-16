package com.hankcs.nlp.dictionary;

import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.util.LogUtil;

/**
 * 核心词典词性转移矩阵，用于HMM
 */
public class CoreDictionaryTransformMatrixDictionary {
    public static TransformMatrixDictionary<Nature> transformMatrixDictionary;

    static {
        transformMatrixDictionary = new TransformMatrixDictionary<Nature>(Nature.class);
        long start = System.currentTimeMillis();
        if (!transformMatrixDictionary.load(Config.CoreDictionaryTransformMatrixDictionaryPath)) {
            throw new IllegalArgumentException("加载核心词典词性转移矩阵" + Config.CoreDictionaryTransformMatrixDictionaryPath + "失败");
        } else {
            LogUtil.logger.info("加载核心词典词性转移矩阵" + Config.CoreDictionaryTransformMatrixDictionaryPath + "成功，耗时：" + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
