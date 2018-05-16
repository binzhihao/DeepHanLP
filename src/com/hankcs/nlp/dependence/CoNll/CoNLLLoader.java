package com.hankcs.nlp.dependence.CoNll;

import com.hankcs.nlp.dependence.io.IOUtil;

import java.util.LinkedList;

/**
 * CoNLL是NLP领域著名的评测每年举办NLP领域的赛事。这部分移植用来做一些基准测试。2018.03.01
 * CoNLL格式依存语料加载
 *
 * @author hankcs
 */
public class CoNLLLoader {
    public static LinkedList<CoNLLSentence> loadSentenceList(String path) {
        LinkedList<CoNLLSentence> result = new LinkedList<CoNLLSentence>();
        LinkedList<CoNllLine> lineList = new LinkedList<CoNllLine>();
        for (String line : IOUtil.readLineListWithLessMemory(path)) {
            if (line.trim().length() == 0) {
                result.add(new CoNLLSentence(lineList));
                lineList = new LinkedList<CoNllLine>();
                continue;
            }
            lineList.add(new CoNllLine(line.split("\t")));
        }

        return result;
    }
}
