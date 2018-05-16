package com.hankcs.nlp.extractor;

import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.extractor.base.AbstractExtractor;

import java.util.List;

/**
 * 暂时注释掉，文档数据较少的情况下，效果不如 TextRank
 */
public class IdpExtractor extends AbstractExtractor {
    @Override
    public List<String> getKeyword(List<Term> termList) {
        return null;
    }

    @Override
    public List<String> getKeyword(List<Term> termList, int count) {
        return null;
    }
}
