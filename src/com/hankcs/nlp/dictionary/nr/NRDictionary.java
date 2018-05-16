package com.hankcs.nlp.dictionary.nr;


import com.hankcs.nlp.dictionary.base.item.EnumItem;
import com.hankcs.nlp.dictionary.base.tag.NR;
import com.hankcs.nlp.dictionary.common.EnumItemDictionary;

import java.util.TreeMap;

/**
 * 一个好用的人名词典
 */
public class NRDictionary extends EnumItemDictionary<NR> {

    @Override
    protected NR valueOf(String name) {
        return NR.valueOf(name);
    }

    @Override
    protected NR[] values() {
        return NR.values();
    }

    @Override
    protected EnumItem<NR> newItem() {
        return new EnumItem<NR>();
    }

    @Override
    protected void onLoaded(TreeMap<String, EnumItem<NR>> map) {
        map.put(" ", new EnumItem<NR>(NR.K, NR.A)); // txt中不允许出现空格词条，这里补上
    }
}
