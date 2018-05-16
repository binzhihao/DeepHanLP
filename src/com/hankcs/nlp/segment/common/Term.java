package com.hankcs.nlp.segment.common;

import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.util.LexiconUtil;

/**
 * 一个单词，用户可以直接访问此单词的全部属性
 */
public class Term {
    /**
     * 词语
     */
    public String word;

    /**
     * 词性
     */
    public Nature nature;

    /**
     * 在文本中的起始位置
     */
    public int offset;

    /**
     * 构造一个单词
     *
     * @param word   词语
     * @param nature 词性
     */
    public Term(String word, Nature nature) {
        this.word = word;
        this.nature = nature;
    }

    @Override
    public String toString() {
        if (Config.ShowTermNature)
            return word + "/" + nature;
        return word;
    }

    /**
     * 长度
     *
     * @return
     */
    public int length() {
        return word.length();
    }

    /**
     * 获取本词语在词库中的频次
     *
     * @return 频次，0代表这是个OOV(out of vocabulary) 未登录词
     */
    public int getFrequency() {
        return LexiconUtil.getFrequency(word);
    }


    /**
     * 判断Term是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term term = (Term) obj;
            if (this.nature == term.nature && this.word.equals(term.word)) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
