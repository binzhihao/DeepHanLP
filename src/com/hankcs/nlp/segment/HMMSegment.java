package com.hankcs.nlp.segment;

import com.hankcs.nlp.dependence.io.ByteArray;
import com.hankcs.nlp.model.trigram.CharacterBasedGenerativeModel;
import com.hankcs.nlp.pool.GlobalObjectPool;
import com.hankcs.nlp.segment.base.CharacterBasedSegment;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.util.TextUtility;

import java.util.LinkedList;
import java.util.List;

import static com.hankcs.nlp.util.LogUtil.logger;

/**
 * 基于字切分，参考论文实现，效果不保证，按照论文的说法效果很好，需要加载外部模型，需要加载外部模型，暂时没有用上
 * 基于2阶HMM（A Second-Order Hidden Markov Model, TriGram3阶文法模型）+ BMES序列标注的分词器
 */
public class HMMSegment extends CharacterBasedSegment {
    CharacterBasedGenerativeModel model;

    public HMMSegment() {
        String modelPath = config.HMMSegmentModelPath;
        model = GlobalObjectPool.get(modelPath);
        if (model != null) return;
        model = new CharacterBasedGenerativeModel();
        long start = System.currentTimeMillis();
        logger.info("开始从[ " + modelPath + " ]加载2阶HMM模型");
        try {
            ByteArray byteArray = ByteArray.createByteArray(modelPath);
            if (byteArray == null) {
                throw new IllegalArgumentException("HMM分词模型[ " + modelPath + " ]不存在");
            }
            model.load(byteArray);
        } catch (Exception e) {
            throw new IllegalArgumentException("发生了异常：" + TextUtility.exceptionToString(e));
        }
        logger.info("加载成功，耗时：" + (System.currentTimeMillis() - start) + " ms");
        GlobalObjectPool.put(modelPath, model);
    }

    @Override
    protected List<Term> roughSegSentence(char[] sentence) {
        char[] tag = model.tag(sentence);
        List<Term> termList = new LinkedList<Term>();
        int offset = 0;
        for (int i = 0; i < tag.length; offset += 1, ++i) {
            switch (tag[i]) {
                case 'b': {
                    int begin = offset;
                    while (tag[i] != 'e') {
                        offset += 1;
                        ++i;
                        if (i == tag.length) {
                            break;
                        }
                    }
                    if (i == tag.length) {
                        termList.add(new Term(new String(sentence, begin, offset - begin), null));
                    } else
                        termList.add(new Term(new String(sentence, begin, offset - begin + 1), null));
                }
                break;
                default: {
                    termList.add(new Term(new String(sentence, offset, 1), null));
                }
                break;
            }
        }

        return termList;
    }
}
