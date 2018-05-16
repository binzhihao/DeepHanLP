package com.hankcs.nlp.segment.base;

import com.hankcs.nlp.dictionary.CoreDictionary;
import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.algorithm.Viterbi;
import com.hankcs.nlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import com.hankcs.nlp.dictionary.CustomDictionary;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.segment.common.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 基于字构词的生成式模型分词器基类
 */
public abstract class CharacterBasedSegment extends AbstractSegment {

    public CharacterBasedSegment() {
        super();
    }

    /**
     * 以下方法用于纯分词模型
     * 分词、词性标注联合模型则直接重载segSentence
     */
    @Override
    protected List<Term> segSentence(char[] sentence) {
        if (sentence.length == 0) return Collections.emptyList();
        List<Term> termList = roughSegSentence(sentence);
        if (!(config.ner || config.useCustomDictionary || config.speechTagging))
            return termList;
        List<Vertex> vertexList = toVertexList(termList, true);
        if (config.speechTagging) {
            Viterbi.compute(vertexList, CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary);
            int i = 0;
            for (Term term : termList) {
                if (term.nature != null) term.nature = vertexList.get(i + 1).guessNature();
                ++i;
            }
        }
        /*if (config.useCustomDictionary) {
            combineByCustomDictionary(vertexList);
            termList = convert(vertexList, config.offset);
        }*/
        return termList;
    }

    /**
     * 查询或猜测一个词语的属性，
     * 先查词典，然后对字母、数字串的属性进行判断，最后猜测未登录词
     *
     * @param term term
     * @return return
     */
    private static CoreDictionary.Attribute guessAttribute(Term term) {
        CoreDictionary.Attribute attribute = CoreDictionary.get(term.word);
        if (attribute == null) {
            attribute = CustomDictionary.get(term.word);
        }
        if (attribute == null) {
            if (term.nature != null) {
                if (Nature.nx == term.nature)
                    attribute = new CoreDictionary.Attribute(Nature.nx);
                else if (Nature.m == term.nature)
                    attribute = CoreDictionary.get(CoreDictionary.M_WORD_ID);
            } else if (term.word.trim().length() == 0)
                attribute = new CoreDictionary.Attribute(Nature.x);
            else attribute = new CoreDictionary.Attribute(Nature.nz);
        } else term.nature = attribute.nature[0];
        return attribute;
    }

    /**
     * 单纯的分词模型实现该方法，仅输出词
     */
    protected abstract List<Term> roughSegSentence(char[] sentence);

    /**
     * 将中间结果转换为词网顶点,
     * 这样就可以利用基于Vertex开发的功能, 如词性标注、NER等
     */
    private List<Vertex> toVertexList(List<Term> wordList, boolean appendStart) {
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>(wordList.size() + 2);
        if (appendStart) vertexList.add(Vertex.newB());
        for (Term word : wordList) {
            CoreDictionary.Attribute attribute = guessAttribute(word);
            Vertex vertex = new Vertex(word.word, attribute);
            vertexList.add(vertex);
        }
        if (appendStart) vertexList.add(Vertex.newE());
        return vertexList;
    }

    /**
     * 将一条路径转为最终结果
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
}
