package com.hankcs.nlp.dependence.CoNll;

/**
 * CoNLL是NLP领域著名的评测每年举办NLP领域的赛事。这部分移植用来做一些基准测试。2018.03.01
 * CoNLL语料中的一行
 *
 * @author hankcs
 */
public class CoNllLine {
    /**
     * 十个值
     */
    public String[] value = new String[10];

    /**
     * 第一个值化为id
     */
    public int id;

    public CoNllLine(String... args) {
        int length = Math.min(args.length, value.length);
        for (int i = 0; i < length; ++i) {
            value[i] = args[i];
        }
        id = Integer.parseInt(value[0]);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String value : this.value) {
            sb.append(value);
            sb.append('\t');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
