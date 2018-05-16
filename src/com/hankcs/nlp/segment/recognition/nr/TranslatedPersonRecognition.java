package com.hankcs.nlp.segment.recognition.nr;

import com.hankcs.nlp.dictionary.CoreDictionary;
import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.dictionary.nr.NRConstant;
import com.hankcs.nlp.segment.common.Vertex;
import com.hankcs.nlp.segment.common.WordNet;
import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.dictionary.nr.TranslatedPersonDictionary;
import com.hankcs.nlp.common.Predefine;

import java.util.List;
import java.util.ListIterator;

/**
 * 直接移植过来的音译人名识别
 */
public class TranslatedPersonRecognition {
    /**
     * 执行识别
     *
     * @param segResult      粗分结果
     * @param wordNetOptimum 粗分结果对应的词图
     * @param wordNetAll     全词图
     */
    public static void Recognition(List<Vertex> segResult, WordNet wordNetOptimum, WordNet wordNetAll) {
        StringBuilder sbName = new StringBuilder();
        int appendTimes = 0;
        ListIterator<Vertex> listIterator = segResult.listIterator();
        listIterator.next();
        int line = 1;
        int activeLine = 1;
        while (listIterator.hasNext()) {
            Vertex vertex = listIterator.next();
            if (appendTimes > 0) {
                if (vertex.guessNature() == Nature.nrf || TranslatedPersonDictionary.containsKey(vertex.realWord)) {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                } else {
                    // 识别结束
                    if (appendTimes > 1) {
                        if (Config.DEBUG) {
                            System.out.println("音译人名识别出：" + sbName.toString());
                        }
                        wordNetOptimum.insert(activeLine, new Vertex(Predefine.TAG_PEOPLE, sbName.toString(), new CoreDictionary.Attribute(Nature.nrf), NRConstant.WORD_ID), wordNetAll);
                    }
                    sbName.setLength(0);
                    appendTimes = 0;
                }
            } else {
                // nrf触发识别
                if (vertex.guessNature() == Nature.nrf
//                        || TranslatedPersonDictionary.containsKey(vertex.realWord)
                        ) {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                    activeLine = line;
                }
            }

            line += vertex.realWord.length();
        }
    }
}
