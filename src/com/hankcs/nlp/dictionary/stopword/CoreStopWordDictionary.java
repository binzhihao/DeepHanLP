package com.hankcs.nlp.dictionary.stopword;

import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.dependence.io.ByteArray;
import com.hankcs.nlp.dependence.io.IOUtil;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.common.Predefine;
import com.hankcs.nlp.util.TextUtility;

import java.io.DataOutputStream;
import java.util.List;
import java.util.ListIterator;

import static com.hankcs.nlp.util.LogUtil.logger;

public class CoreStopWordDictionary {
    static StopWordDictionary dictionary;

    static {
        ByteArray byteArray = ByteArray.createByteArray(Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT);
        logger.info("停用词词典开始加载：" + Config.CoreStopWordDictionaryPath);
        if (byteArray == null) {
            try {
                dictionary = new StopWordDictionary(Config.CoreStopWordDictionaryPath);
                DataOutputStream out = new DataOutputStream(IOUtil.newOutputStream(Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT));
                dictionary.save(out);
                out.close();
                logger.info("停用词词典加载成功");
            } catch (Exception e) {
                logger.severe("载入停用词词典" + Config.CoreStopWordDictionaryPath + "失败" + TextUtility.exceptionToString(e));
            }
        } else {
            dictionary = new StopWordDictionary();
            dictionary.load(byteArray);
            logger.info("停用词词典加载成功");
        }
    }

    public static boolean contains(String key) {
        return dictionary.contains(key);
    }

    /**
     * 核心停用词典的核心过滤器，词性属于名词、动词、副词、形容词，并且不在停用词表中才不会被过滤
     */
    public static Filter FILTER = new Filter() {
        @Override
        public boolean shouldInclude(Term term) {
            // 除掉停用词
            String nature = term.nature != null ? term.nature.toString() : "空";
            char firstChar = nature.charAt(0);
            switch (firstChar) {
                case 'm':
                case 'b':
                case 'c':
                case 'e':
                case 'o':
                case 'p':
                case 'q':
                case 'u':
                case 'y':
                case 'z':
                case 'r':
                case 'w': {
                    return false;
                }
                default: {
                    if (!CoreStopWordDictionary.contains(term.word)) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }
    };

    /**
     * 是否应当将这个term纳入计算
     *
     * @param term
     * @return 是否应当
     */
    public static boolean shouldInclude(Term term) {
        return FILTER.shouldInclude(term);
    }

    /**
     * 是否应当去掉这个词
     *
     * @param term 词
     * @return 是否应当去掉
     */
    public static boolean shouldRemove(Term term) {
        return !shouldInclude(term);
    }

    /**
     * 加入停用词到停用词词典中
     *
     * @param stopWord 停用词
     * @return 词典是否发生了改变
     */
    public static boolean add(String stopWord) {
        return dictionary.add(stopWord);
    }

    /**
     * 从停用词词典中删除停用词
     *
     * @param stopWord 停用词
     * @return 词典是否发生了改变
     */
    public static boolean remove(String stopWord) {
        return dictionary.remove(stopWord);
    }

    /**
     * 对分词结果应用过滤
     *
     * @param termList
     */
    public static void apply(List<Term> termList) {
        ListIterator<Term> listIterator = termList.listIterator();
        while (listIterator.hasNext()) {
            if (shouldRemove(listIterator.next())) listIterator.remove();
        }
    }
}
