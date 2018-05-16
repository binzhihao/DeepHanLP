package com.hankcs.nlp.dependence.CoNll;

import java.util.Iterator;
import java.util.List;

/**
 * CoNLL是NLP领域著名的评测每年举办NLP领域的赛事。这部分移植用来做一些基准测试。2018.03.01
 * CoNLL中的一个句子
 *
 * @author hankcs
 */
public class CoNLLSentence implements Iterable<CoNLLWord> {
    /**
     * 有许多行，每行是一个单词
     */
    public CoNLLWord[] word;

    /**
     * 构造一个句子
     *
     * @param lineList
     */
    public CoNLLSentence(List<CoNllLine> lineList) {
        CoNllLine[] lineArray = lineList.toArray(new CoNllLine[0]);
        this.word = new CoNLLWord[lineList.size()];
        int i = 0;
        for (CoNllLine line : lineList) {
            word[i++] = new CoNLLWord(line);
        }
        for (CoNLLWord nllWord : word) {
            int head = Integer.parseInt(lineArray[nllWord.ID - 1].value[6]) - 1;
            if (head != -1) {
                nllWord.HEAD = word[head];
            } else {
                nllWord.HEAD = CoNLLWord.ROOT;
            }
        }
    }

    public CoNLLSentence(CoNLLWord[] word) {
        this.word = word;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(word.length * 50);
        for (CoNLLWord word : this.word) {
            sb.append(word);
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * 获取边的列表，edge[i][j]表示id为i的词语与j存在一条依存关系为该值的边，否则为null
     *
     * @return
     */
    public String[][] getEdgeArray() {
        String[][] edge = new String[word.length + 1][word.length + 1];
        for (CoNLLWord coNLLWord : word) {
            edge[coNLLWord.ID][coNLLWord.HEAD.ID] = coNLLWord.DEPREL;
        }

        return edge;
    }

    /**
     * 获取包含根节点在内的单词数组
     *
     * @return
     */
    public CoNLLWord[] getWordArrayWithRoot() {
        CoNLLWord[] wordArray = new CoNLLWord[word.length + 1];
        wordArray[0] = CoNLLWord.ROOT;
        System.arraycopy(word, 0, wordArray, 1, word.length);

        return wordArray;
    }

    public CoNLLWord[] getWordArray() {
        return word;
    }

    @Override
    public Iterator<CoNLLWord> iterator() {
        return new Iterator<CoNLLWord>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < word.length;
            }

            @Override
            public CoNLLWord next() {
                return word[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("CoNLLSentence是只读对象，不允许删除");
            }
        };
    }
}
