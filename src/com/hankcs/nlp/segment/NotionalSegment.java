package com.hankcs.nlp.segment;

import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.NLPManager;
import com.hankcs.nlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.nlp.segment.base.WordBasedSegment;

import java.util.List;
import java.util.ListIterator;

/**
 * 实词分词器，自动移除停用词
 */
public class NotionalSegment extends WordBasedSegment {

    @Override
    protected List<Term> segSentence(char[] text) {
        List<Term> resultList = NLPManager.getInstance().getSegment().seg(text);    //还是使用默认的分词器好了，分词后去掉一些词
        ListIterator<Term> listIterator = resultList.listIterator();
        while (listIterator.hasNext()) {
            if (!CoreStopWordDictionary.shouldInclude(listIterator.next())) {
                listIterator.remove();
            }
        }
        return resultList;
    }
}
