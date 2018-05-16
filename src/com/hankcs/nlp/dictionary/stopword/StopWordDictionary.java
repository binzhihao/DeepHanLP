package com.hankcs.nlp.dictionary.stopword;

import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.collection.graph.MDAG.MDAGSet;

import java.io.*;
import java.util.Collection;

public class StopWordDictionary extends MDAGSet implements Filter {
    public StopWordDictionary(File file) throws IOException {
        super(file);
    }

    public StopWordDictionary(Collection<String> strCollection) {
        super(strCollection);
    }

    public StopWordDictionary() {
    }

    public StopWordDictionary(String stopWordDictionaryPath) throws IOException {
        super(stopWordDictionaryPath);
    }

    @Override
    public boolean shouldInclude(Term term) {
        return contains(term.word);
    }
}
