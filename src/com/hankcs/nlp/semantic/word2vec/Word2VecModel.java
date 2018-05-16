package com.hankcs.nlp.semantic.word2vec;

import com.hankcs.nlp.semantic.word2vec.base.AbstractVectorModel;
import com.hankcs.nlp.semantic.word2vec.base.Vector;
import com.hankcs.nlp.semantic.word2vec.util.VectorsReader;

import java.io.IOException;
import java.util.*;

/**
 * 词向量模型
 */
public class Word2VecModel extends AbstractVectorModel<String> {
    /**
     * 加载模型
     *
     * @param modelFileName 模型路径
     * @throws IOException 加载错误
     */
    public Word2VecModel(String modelFileName) throws IOException {
        super(loadVectorMap(modelFileName));
    }

    private static TreeMap<String, Vector> loadVectorMap(String modelFileName) throws IOException {
        VectorsReader reader = new VectorsReader(modelFileName);
        reader.readVectorFile();
        TreeMap<String, Vector> map = new TreeMap<String, Vector>();
        for (int i = 0; i < reader.vocab.length; i++) {
            map.put(reader.vocab[i], new Vector(reader.matrix[i]));
        }
        return map;
    }

    /**
     * 返回跟 A - B + C 最相似的词语,比如 中国 - 北京 + 东京 = 日本。输入顺序按照 中国 北京 东京
     *
     * @param A 做加法的词语
     * @param B 做减法的词语
     * @param C 做加法的词语
     * @return 与(A - B + C)语义距离最近的词语及其相似度列表
     */
    public List<Map.Entry<String, Float>> analogy(String A, String B, String C) {
        return analogy(A, B, C, 10);
    }

    /**
     * 返回跟 A - B + C 最相似的词语,比如 中国 - 北京 + 东京 = 日本。输入顺序按照 中国 北京 东京
     *
     * @param A    做加法的词语
     * @param B    做减法的词语
     * @param C    做加法的词语
     * @param size topN个
     * @return 与(A - B + C)语义距离最近的词语及其相似度列表
     */
    public List<Map.Entry<String, Float>> analogy(String A, String B, String C, int size) {
        Vector a = storage.get(A);
        Vector b = storage.get(B);
        Vector c = storage.get(C);
        if (a == null || b == null || c == null) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, Float>> resultList = nearest(a.minus(b).add(c), size + 3);
        ListIterator<Map.Entry<String, Float>> listIterator = resultList.listIterator();
        while (listIterator.hasNext()) {
            String key = listIterator.next().getKey();
            if (key.equals(A) || key.equals(B) || key.equals(C)) {
                listIterator.remove();
            }
        }

        if (resultList.size() > size) {
            resultList = resultList.subList(0, size);
        }

        return resultList;
    }

    @Override
    public Vector query(String query) {
        return vector(query);
    }
}