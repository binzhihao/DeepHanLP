package com.hankcs.nlp.segment;

import com.hankcs.nlp.segment.recognition.nr.PersonRecognition;
import com.hankcs.nlp.segment.recognition.nr.TranslatedPersonRecognition;
import com.hankcs.nlp.segment.recognition.ns.PlaceRecognition;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.segment.common.Vertex;
import com.hankcs.nlp.segment.common.WordNet;
import com.hankcs.nlp.segment.base.WordBasedSegment;

import java.util.LinkedList;
import java.util.List;

/**
 * 基于概率图模型，HMM-Viterbi，本质上还是基于NGram的分词方法
 */
public class ViterbiSegment extends WordBasedSegment {

    @Override
    protected List<Term> segSentence(char[] sentence) {

        // 生成原始词网，这里只是简单进行结构初始化，带入原始文本的字符数组
        WordNet wordNetAll = new WordNet(sentence);
        // 生成一元词网
        GenerateWordNet(wordNetAll);
        // 隐式马尔科夫链，一阶隐马
        List<Vertex> vertexList = viterbi(wordNetAll);

        /*if (config.useCustomDictionary) {
            if (config.indexMode)
                combineByCustomDictionary(vertexList, wordNetAll);
            else combineByCustomDictionary(vertexList);
        }*/

        // 实体命名识别
        if (config.ner) {
            WordNet wordNetOptimum = new WordNet(sentence, vertexList);
            int preSize = wordNetOptimum.size();
            if (config.nameRecognize) {
                PersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.translatedNameRecognize) {
                TranslatedPersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.placeRecognize) {
                PlaceRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (wordNetOptimum.size() != preSize) {
                // 细分词网
                vertexList = viterbi(wordNetOptimum);
            }
        }

        // 标注词性
        if (config.speechTagging) {
            speechTagging(vertexList);
        }

        return convert(vertexList);
    }

    private static List<Vertex> viterbi(WordNet wordNet) {
        // 避免生成对象，优化速度
        LinkedList<Vertex> nodes[] = wordNet.getVertexes();
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();
        for (Vertex node : nodes[1]) {
            node.updateFrom(nodes[0].getFirst());
        }
        for (int i = 1; i < nodes.length - 1; ++i) {
            LinkedList<Vertex> nodeArray = nodes[i];
            if (nodeArray == null) continue;
            for (Vertex node : nodeArray) {
                if (node.from == null) continue;
                for (Vertex to : nodes[i + node.realWord.length()]) {
                    to.updateFrom(node);
                }
            }
        }
        Vertex from = nodes[nodes.length - 1].getFirst();
        while (from != null) {
            vertexList.addFirst(from);
            from = from.from;
        }
        return vertexList;
    }

}
