package com.hankcs.nlp.dictionary.nt;


import com.hankcs.nlp.dictionary.base.item.EnumItem;
import com.hankcs.nlp.dictionary.base.tag.NT;
import com.hankcs.nlp.dictionary.common.EnumItemDictionary;

/**
 * 一个好用的机构名词典
 */
public class NTDictionary extends EnumItemDictionary<NT> {
    @Override
    protected NT valueOf(String name) {
        return NT.valueOf(name);
    }

    @Override
    protected NT[] values() {
        return NT.values();
    }

    @Override
    protected EnumItem<NT> newItem() {
        return new EnumItem<NT>();
    }
}
