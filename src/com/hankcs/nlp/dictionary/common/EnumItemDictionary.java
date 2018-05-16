package com.hankcs.nlp.dictionary.common;

import com.hankcs.nlp.dictionary.base.item.EnumItem;
import com.hankcs.nlp.dependence.io.ByteArray;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 枚举条目的通用词典
 */
public abstract class EnumItemDictionary<E extends Enum<E>> extends CommonDictionary<EnumItem<E>> {
    @Override
    protected EnumItem<E> createValue(String[] params) {
        Map.Entry<String, Map.Entry<String, Integer>[]> args = EnumItem.create(params);
        EnumItem<E> nrEnumItem = new EnumItem<E>();
        for (Map.Entry<String, Integer> e : args.getValue()) {
            nrEnumItem.labelMap.put(valueOf(e.getKey()), e.getValue());
        }
        return nrEnumItem;
    }

    /**
     * 代理E.valueOf
     *
     * @param name
     * @return
     */
    protected abstract E valueOf(String name);

    /**
     * 代理E.values
     *
     * @return
     */
    protected abstract E[] values();

    /**
     * 代理new EnumItem<E>
     *
     * @return
     */
    protected abstract EnumItem<E> newItem();

    @Override
    final protected EnumItem<E>[] loadValueArray(ByteArray byteArray) {
        if (byteArray == null) {
            return null;
        }
        E[] nrArray = values();
        int size = byteArray.nextInt();
        EnumItem<E>[] valueArray = new EnumItem[size];
        for (int i = 0; i < size; ++i) {
            int currentSize = byteArray.nextInt();
            EnumItem<E> item = newItem();
            for (int j = 0; j < currentSize; ++j) {
                E nr = nrArray[byteArray.nextInt()];
                int frequency = byteArray.nextInt();
                item.labelMap.put(nr, frequency);
            }
            valueArray[i] = item;
        }
        return valueArray;
    }

    @Override
    protected void saveValue(EnumItem<E> item, DataOutputStream out) throws IOException {
        out.writeInt(item.labelMap.size());
        for (Map.Entry<E, Integer> entry : item.labelMap.entrySet()) {
            out.writeInt(entry.getKey().ordinal());
            out.writeInt(entry.getValue());
        }
    }
}
