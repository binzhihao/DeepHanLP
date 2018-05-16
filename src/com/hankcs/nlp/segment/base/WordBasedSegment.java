package com.hankcs.nlp.segment.base;

import com.hankcs.nlp.dictionary.CoreDictionary;
import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.algorithm.Viterbi;
import com.hankcs.nlp.collection.trie.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.nlp.collection.trie.DoubleArrayTrie.DoubleArrayTrie;
import com.hankcs.nlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import com.hankcs.nlp.dictionary.CustomDictionary;
import com.hankcs.nlp.dictionary.other.CharType;
import com.hankcs.nlp.segment.common.AtomNode;
import com.hankcs.nlp.segment.common.Graph;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.segment.common.Vertex;
import com.hankcs.nlp.segment.common.WordNet;
import com.hankcs.nlp.util.TextUtility;

import java.util.*;

/**
 * 基于词语NGram模型的分词器基类
 */
public abstract class WordBasedSegment extends AbstractSegment {

    public WordBasedSegment() {
        super();
    }

    /**
     * 一元切分，生成一元词网
     *
     * @param wordNetStorage 原始词网
     */
    protected void GenerateWordNet(final WordNet wordNetStorage) {
        final char[] charArray = wordNetStorage.charArray;

        // 核心词典查询，填充Trie树
        DoubleArrayTrie<CoreDictionary.Attribute>.Searcher searcher = CoreDictionary.trie.getSearcher(charArray, 0);
        while (searcher.next()) {
            wordNetStorage.add(searcher.begin + 1, new Vertex(new String(charArray, searcher.begin, searcher.length), searcher.value, searcher.index));
        }
        // 强制用户词典查询，注意用户词典为后续脚本生成的词典或者补充词典，加入效果不一定更好，主要用于新词
        if (config.forceCustomDictionary) {
            CustomDictionary.parseText(charArray, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>() {
                @Override
                public void hit(int begin, int end, CoreDictionary.Attribute value) {
                    wordNetStorage.add(begin + 1, new Vertex(new String(charArray, begin, end - begin), value));
                }
            });
        }
        // 原子分词，保证图连通
        LinkedList<Vertex>[] vertexes = wordNetStorage.getVertexes();
        for (int i = 1; i < vertexes.length; ) {
            if (vertexes[i].isEmpty()) {
                int j = i + 1;
                for (; j < vertexes.length - 1; ++j) {
                    if (!vertexes[j].isEmpty()) break;
                }
                wordNetStorage.add(i, quickAtomSegment(charArray, i - 1, j - 1));
                i = j;
            } else i += vertexes[i].getLast().realWord.length();
        }
    }

    /**
     * 原子切分
     */
    protected static List<AtomNode> quickAtomSegment(char[] charArray, int start, int end) {
        List<AtomNode> atomNodeList = new LinkedList<AtomNode>();
        int offsetAtom = start;
        int preType = CharType.get(charArray[offsetAtom]);
        int curType;
        while (++offsetAtom < end) {
            curType = CharType.get(charArray[offsetAtom]);
            if (curType != preType) {
                // 浮点数识别
                if (preType == CharType.CT_NUM && "，,．.".indexOf(charArray[offsetAtom]) != -1) {
                    if (offsetAtom + 1 < end) {
                        int nextType = CharType.get(charArray[offsetAtom + 1]);
                        if (nextType == CharType.CT_NUM) {
                            continue;
                        }
                    }
                }
                atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));
                start = offsetAtom;
            }
            preType = curType;
        }
        if (offsetAtom == end)
            atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));

        return atomNodeList;
    }

    /**
     * 二元切分，生成二元词图
     *
     * @param wordNet 一元词网
     */
    protected static Graph GenerateBiGraph(WordNet wordNet) {
        return wordNet.toGraph();
    }

    /**
     * 对粗分结果执行一些规则上的合并拆分等等，同时合成新词网，主要是匹配未登录词
     *
     * @param linkedArray    粗分结果
     * @param wordNetOptimum 合并了所有粗分结果的词网
     */
    protected static void GenerateWord(List<Vertex> linkedArray, WordNet wordNetOptimum) {
        fixResultByRule(linkedArray);
        wordNetOptimum.addAll(linkedArray);
    }

    /**
     * 词性标注
     */
    protected static void speechTagging(List<Vertex> vertexList) {
        Viterbi.compute(vertexList, CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary);
    }

    /**
     * 将一条路径转为最终结果
     */
    protected static List<Term> convert(List<Vertex> vertexList) {
        return convert(vertexList, false);
    }

    /**
     * 将一条路径转为最终结果
     *
     * @param vertexList    list
     * @param offsetEnabled 是否计算offset
     */
    protected static List<Term> convert(List<Vertex> vertexList, boolean offsetEnabled) {
        assert vertexList != null;
        assert vertexList.size() >= 2 : "这条路径不应当短于2" + vertexList.toString();

        int length = vertexList.size() - 2;
        List<Term> resultList = new ArrayList<Term>(length);
        Iterator<Vertex> iterator = vertexList.iterator();
        iterator.next();
        if (offsetEnabled) {
            int offset = 0;
            for (int i = 0; i < length; ++i) {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                term.offset = offset;
                offset += term.length();
                resultList.add(term);
            }
        } else {
            for (int i = 0; i < length; ++i) {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                resultList.add(term);
            }
        }
        return resultList;
    }

    /**
     * 将节点转为term
     */
    private static Term convert(Vertex vertex) {
        return new Term(vertex.realWord, vertex.guessNature());
    }

    private static void fixResultByRule(List<Vertex> linkedArray) {

        //合并连续的数字
        mergeContinueNumIntoOne(linkedArray);

        ChangeDelimiterPOS(linkedArray);

        //如果前一个词是数字，当前词以“－”或“-”开始，并且不止这一个字符，
        //那么将此“－”符号从当前词中分离出来。
        //例如 “3 / -4 / 月”需要拆分成“3 / - / 4 / 月”
        SplitMiddleSlashFromDigitalWords(linkedArray);

        //1、如果当前词是数字，下一个词是“月、日、时、分、秒、月份”中的一个，则合并,且当前词词性是时间
        //2、如果当前词是可以作为年份的数字，下一个词是“年”，则合并，词性为时间，否则为数字。
        //3、如果最后一个汉字是"点" ，则认为当前数字是时间
        //4、如果当前串最后一个汉字不是"∶·．／"和半角的'.''/'，那么是数
        //5、当前串最后一个汉字是"∶·．／"和半角的'.''/'，且长度大于1，那么去掉最后一个字符。例如"1."
        CheckDateElements(linkedArray);
    }

    private static void mergeContinueNumIntoOne(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2)
            return;

        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            if ((TextUtility.isAllNum(current.realWord) || TextUtility.isAllChineseNum(current.realWord)) && (TextUtility.isAllNum(next.realWord) || TextUtility.isAllChineseNum(next.realWord))) {
                // 这部分从逻辑上等同于current.realWord = current.realWord + next.realWord;
                // 但是current指针被几个路径共享，需要备份，不然修改了一处就修改了全局
                current = Vertex.newNumberInstance(current.realWord + next.realWord);
                listIterator.previous();
                listIterator.previous();
                listIterator.set(current);
                listIterator.next();
                listIterator.next();
                listIterator.remove();
            } else {
                current = next;
            }
        }
    }

    private static void ChangeDelimiterPOS(List<Vertex> linkedArray) {
        for (Vertex vertex : linkedArray) {
            if (vertex.realWord.equals("－－") || vertex.realWord.equals("—") || vertex.realWord.equals("-")) {
                vertex.confirmNature(Nature.w);
            }
        }
    }

    private static void SplitMiddleSlashFromDigitalWords(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2)
            return;

        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            Nature currentNature = current.getNature();
            if (currentNature == Nature.nx && (next.hasNature(Nature.q) || next.hasNature(Nature.n))) {
                String[] param = current.realWord.split("-", 1);
                if (param.length == 2) {
                    if (TextUtility.isAllNum(param[0]) && TextUtility.isAllNum(param[1])) {
                        current = current.copy();
                        current.realWord = param[0];
                        current.confirmNature(Nature.m);
                        listIterator.previous();
                        listIterator.previous();
                        listIterator.set(current);
                        listIterator.next();
                        listIterator.add(Vertex.newPunctuationInstance("-"));
                        listIterator.add(Vertex.newNumberInstance(param[1]));
                    }
                }
            }
            current = next;
        }
    }

    private static void CheckDateElements(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2)
            return;
        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            if (TextUtility.isAllNum(current.realWord) || TextUtility.isAllChineseNum(current.realWord)) {
                String nextWord = next.realWord;
                if ((nextWord.length() == 1 && "月日时分秒".contains(nextWord)) || (nextWord.length() == 2 && nextWord.equals("月份"))) {
                    current = Vertex.newTimeInstance(current.realWord + next.realWord);
                    listIterator.previous();
                    listIterator.previous();
                    listIterator.set(current);
                    listIterator.next();
                    listIterator.next();
                    listIterator.remove();
                } else if (nextWord.equals("年")) {
                    if (TextUtility.isYearTime(current.realWord)) {
                        current = Vertex.newTimeInstance(current.realWord + next.realWord);
                        listIterator.previous();
                        listIterator.previous();
                        listIterator.set(current);
                        listIterator.next();
                        listIterator.next();
                        listIterator.remove();
                    } else {
                        current.confirmNature(Nature.m);
                    }
                } else {
                    if (current.realWord.endsWith("点")) {
                        current.confirmNature(Nature.t, true);
                    } else {
                        char[] tmpCharArray = current.realWord.toCharArray();
                        String lastChar = String.valueOf(tmpCharArray[tmpCharArray.length - 1]);
                        if (!"∶·．／./".contains(lastChar)) {
                            current.confirmNature(Nature.m, true);
                        } else if (current.realWord.length() > 1) {
                            char last = current.realWord.charAt(current.realWord.length() - 1);
                            current = Vertex.newNumberInstance(current.realWord.substring(0, current.realWord.length() - 1));
                            listIterator.previous();
                            listIterator.previous();
                            listIterator.set(current);
                            listIterator.next();
                            listIterator.add(Vertex.newPunctuationInstance(String.valueOf(last)));
                        }
                    }
                }
            }
            current = next;
        }
    }

}
