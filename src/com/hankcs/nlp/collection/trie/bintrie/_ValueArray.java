package com.hankcs.nlp.collection.trie.bintrie;

/**
 * 对值数组的包装，可以方便地取下一个
 */
public class _ValueArray<V> {
    V[] value;
    int offset;

    public _ValueArray(V[] value) {
        this.value = value;
    }

    public V nextValue() {
        return value[offset++];
    }

    /**
     * 仅供子类使用
     */
    protected _ValueArray() {
    }

    public _ValueArray setValue(V[] value) {
        this.value = value;
        return this;
    }
}
