package com.hankcs.nlp.extractor.base;

import com.hankcs.nlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.nlp.segment.common.Term;

import java.util.List;

public abstract class AbstractExtractor {

    /**
     * 除掉停用词，依赖词性标注
     */
    public boolean shouldInclude(Term term) {
        if (term.nature == null) {
            return false;
        }
        String nature = term.nature.toString();
        char firstChar = nature.charAt(0);
        switch (firstChar) {
            case 'm':
            case 'b':
            case 'c':
            case 'e':
            case 'o':
            case 'p':
            case 'q':
            case 'u':
            case 'y':
            case 'z':
            case 'r':
            case 'w': {
                return false;
            }
            default: {
                if (term.word.trim().length() > 1 && !CoreStopWordDictionary.contains(term.word)) {
                    return true;
                }
            }
            break;
        }

        return false;
    }

    public abstract List<String> getKeyword(List<Term> termList);

    public abstract List<String> getKeyword(List<Term> termList, int count);
}
