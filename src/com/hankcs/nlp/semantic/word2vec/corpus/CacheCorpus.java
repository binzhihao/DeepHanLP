package com.hankcs.nlp.semantic.word2vec.corpus;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 从缓存中加载语料库
 */
public class CacheCorpus extends AbstractCorpus {
    private RandomAccessFile raf;

    public CacheCorpus(AbstractCorpus cloneSrc) throws IOException {
        super(cloneSrc);
        raf = new RandomAccessFile(((TextFileCorpus) cloneSrc).cacheFile, "r");
    }

    @Override
    public String nextWord() throws IOException {
        return null;
    }

    @Override
    public int readWordIndex() throws IOException {
        int id = nextId();
        while (id == -4) {
            id = nextId();
        }
        return id;
    }

    private int nextId() throws IOException {
        if (raf.length() - raf.getFilePointer() >= 4) {
            int id = raf.readInt();
            return id < 0 ? id : table[id];
        }

        return -2;
    }

    @Override
    public void rewind(int numThreads, int id) throws IOException {
        super.rewind(numThreads, id);
        raf.seek(raf.length() / 4 / numThreads * id * 4);   // spilt by id, not by bytes
    }
}