package com.hankcs.nlp.util;

import com.hankcs.nlp.segment.common.Vertex;
import com.hankcs.nlp.dictionary.CoreBiGramTableDictionary;

import static com.hankcs.nlp.common.Predefine.*;

/**
 * 以后数学相关的小工具都放这里
 */
public class MathUtil {
    /**
     * 从一个词到另一个词的词的花费
     *
     * @param from 前面的词
     * @param to   后面的词
     * @return 分数
     */
    public static double calculateWeight(Vertex from, Vertex to) {
        int frequency = from.getAttribute().totalFrequency;
        if (frequency == 0) {
            frequency = 1;  // 防止发生除零错误
        }
        int nTwoWordsFreq = CoreBiGramTableDictionary.getBiFrequency(from.wordID, to.wordID);
        double value = -Math.log(dSmoothingPara * frequency / (MAX_FREQUENCY) + (1 - dSmoothingPara) * ((1 - dTemp) * nTwoWordsFreq / frequency + dTemp));
        if (value < 0.0) {
            value = -value;
        }
        return value;
    }
}
