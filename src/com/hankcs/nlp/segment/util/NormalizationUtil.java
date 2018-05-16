package com.hankcs.nlp.segment.util;

public class NormalizationUtil {

    private static class SingletonHolder {
        private static final NormalizationUtil INSTANCE = new NormalizationUtil();
    }
    
    public static NormalizationUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private NormalizationUtil() {

    }

    /**
     * 做一些规范化处理，比如简繁转换，全角半角转换 等
     */
    public char[] normalize(char[] text) {
        return null;
    }
}
