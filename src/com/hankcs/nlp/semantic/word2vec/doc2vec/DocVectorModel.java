package com.hankcs.nlp.semantic.word2vec.doc2vec;

import com.hankcs.nlp.semantic.word2vec.Word2VecModel;
import com.hankcs.nlp.semantic.word2vec.base.AbstractVectorModel;
import com.hankcs.nlp.semantic.word2vec.base.Vector;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.NLPManager;

import java.util.*;

/**
 * 文档向量模型, 基于词向量，简单的加权平均计算
 */
public class DocVectorModel extends AbstractVectorModel<Integer> {
    private final Word2VecModel word2VecModel;

    public DocVectorModel(Word2VecModel word2VecModel) {
        super();
        this.word2VecModel = word2VecModel;
    }

    /**
     * 添加文档
     *
     * @param id      文档id
     * @param content 文档内容
     * @return 文档向量
     */
    public com.hankcs.nlp.semantic.word2vec.base.Vector addDocument(int id, String content) {
        com.hankcs.nlp.semantic.word2vec.base.Vector result = query(content);
        if (result == null) return null;
        storage.put(id, result);
        return result;
    }


    /**
     * 查询最相似的前10个文档
     *
     * @param query 查询语句（或者说一个文档的内容）
     * @return
     */
    public List<Map.Entry<Integer, Float>> nearest(String query) {
        return queryNearest(query, 10);
    }


    /**
     * 将一个文档转为向量
     *
     * @param content 文档
     * @return 向量
     */
    public com.hankcs.nlp.semantic.word2vec.base.Vector query(String content) {
        if (content == null || content.length() == 0) return null;
        List<Term> termList = NLPManager.getInstance().getSegment(NLPManager.SEG_NOTION).seg(content.toCharArray());
        com.hankcs.nlp.semantic.word2vec.base.Vector result = new com.hankcs.nlp.semantic.word2vec.base.Vector(dimension());
        int n = 0;
        for (Term term : termList) {
            com.hankcs.nlp.semantic.word2vec.base.Vector vector = word2VecModel.vector(term.word);
            if (vector == null) {
                continue;
            }
            ++n;
            result.addToSelf(vector);
        }
        if (n == 0) {
            return null;
        }
        result.normalize();
        return result;
    }

    @Override
    public int dimension() {
        return word2VecModel.dimension();
    }

    /**
     * 文档相似度计算
     *
     * @param what
     * @param with
     * @return
     */
    public float similarity(String what, String with) {
        com.hankcs.nlp.semantic.word2vec.base.Vector A = query(what);
        if (A == null) return -1f;
        Vector B = query(with);
        if (B == null) return -1f;
        return A.cosineForUnitVector(B);
    }
}
