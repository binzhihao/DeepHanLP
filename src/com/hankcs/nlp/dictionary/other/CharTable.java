package com.hankcs.nlp.dictionary.other;

import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.dependence.io.IOUtil;
import com.hankcs.nlp.common.Predefine;

import java.io.ObjectInputStream;

import static com.hankcs.nlp.util.LogUtil.logger;

/**
 * 字符正规化表
 */
public class CharTable {
    /**
     * 正规化使用的对应表
     */
    public static char[] CONVERT;

    static {
        long start = System.currentTimeMillis();
        if (!load(Config.CharTablePath)) {
            throw new IllegalArgumentException("字符正规化表加载失败");
        }
        logger.info("字符正规化表加载成功：" + (System.currentTimeMillis() - start) + " ms");
    }

    private static boolean load(String path) {
        String binPath = path + Predefine.BIN_EXT;
        if (loadBin(binPath)) return true;
        CONVERT = new char[Character.MAX_VALUE + 1];
        for (int i = 0; i < CONVERT.length; i++) {
            CONVERT[i] = (char) i;
        }
        IOUtil.LineIterator iterator = new IOUtil.LineIterator(path);
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line == null) return false;
            if (line.length() != 3) continue;
            CONVERT[line.charAt(0)] = CONVERT[line.charAt(2)];
        }
        logger.info("正在缓存字符正规化表到" + binPath);
        IOUtil.saveObjectTo(CONVERT, binPath);

        return true;
    }

    private static boolean loadBin(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(IOUtil.newInputStream(path));
            CONVERT = (char[]) in.readObject();
            in.close();
        } catch (Exception e) {
            logger.warning("字符正规化表缓存加载失败，原因如下：" + e);
            return false;
        }

        return true;
    }

    /**
     * 将一个字符正规化
     *
     * @param c 字符
     * @return 正规化后的字符
     */
    public static char convert(char c) {
        return CONVERT[c];
    }

    public static char[] convert(char[] charArray) {
        char[] result = new char[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            result[i] = CONVERT[charArray[i]];
        }

        return result;
    }

    public static String convert(String charArray) {
        assert charArray != null;
        char[] result = new char[charArray.length()];
        for (int i = 0; i < charArray.length(); i++) {
            result[i] = CONVERT[charArray.charAt(i)];
        }

        return new String(result);
    }

    /**
     * 正规化一些字符（原地正规化）
     *
     * @param charArray 字符
     */
    public static void normalization(char[] charArray) {
        assert charArray != null;
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = CONVERT[charArray[i]];
        }
    }
}
