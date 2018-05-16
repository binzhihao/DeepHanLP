package com.hankcs.nlp.segment.common;

/**
 * 基础边，不允许构造
 */
public class Edge {
    /**
     * 花费
     */
    public double weight;
    /**
     * 节点名字，调试用
     */
    String name;

    protected Edge(double weight, String name) {
        this.weight = weight;
        this.name = name;
    }
}
