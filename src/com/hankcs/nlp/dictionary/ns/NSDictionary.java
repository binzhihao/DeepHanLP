package com.hankcs.nlp.dictionary.ns;


import com.hankcs.nlp.dictionary.base.item.EnumItem;
import com.hankcs.nlp.dictionary.base.tag.NS;
import com.hankcs.nlp.dictionary.common.EnumItemDictionary;

/**
 * 一个好用的地名词典
 */
public class NSDictionary extends EnumItemDictionary<NS> {
    @Override
    protected NS valueOf(String name) {
        return NS.valueOf(name);
    }

    @Override
    protected NS[] values() {
        return NS.values();
    }

    @Override
    protected EnumItem<NS> newItem() {
        return new EnumItem<NS>();
    }
}
