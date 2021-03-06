package com.hankcs.nlp.dictionary;


import com.hankcs.nlp.collection.trie.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.nlp.collection.trie.DoubleArrayTrie.DoubleArrayTrie;
import com.hankcs.nlp.collection.trie.bintrie.BinTrie;
import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.common.Predefine;
import com.hankcs.nlp.dependence.io.ByteArray;
import com.hankcs.nlp.dependence.io.IOUtil;
import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.dictionary.other.CharTable;
import com.hankcs.nlp.util.LexiconUtil;
import com.hankcs.nlp.util.LogUtil;
import com.hankcs.nlp.util.TextUtility;

import java.io.*;
import java.util.*;

/**
 * 用户自定义词典
 */
public class CustomDictionary {
    /**
     * 用于储存用户动态插入词条的二分trie树
     */
    public static BinTrie<CoreDictionary.Attribute> trie;
    public static DoubleArrayTrie<CoreDictionary.Attribute> dat = new DoubleArrayTrie<CoreDictionary.Attribute>();

    // 自动加载词典。自定义词典可能有很多，论文建议将自定义词典按照词性或者领域拆分成多个文件，方便管理和更新。这里只加载了第一个。
    static {
        String path[] = Config.CustomDictionaryPath;
        long start = System.currentTimeMillis();
        LogUtil.logger.info("自定义词典开始加载:" + path[0]);
        if (!loadMainDictionary(path[0])) {
            LogUtil.logger.warning("自定义词典" + Arrays.toString(path) + "加载失败");
        } else {
            LogUtil.logger.info("自定义词典加载成功:" + dat.size() + "个词条，耗时" + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private static boolean loadMainDictionary(String mainPath) {
        if (loadDat(mainPath)) return true;
        dat = new DoubleArrayTrie<CoreDictionary.Attribute>();
        TreeMap<String, CoreDictionary.Attribute> map = new TreeMap<String, CoreDictionary.Attribute>();
        LinkedHashSet<Nature> customNatureCollector = new LinkedHashSet<Nature>();
        try {
            String path[] = Config.CustomDictionaryPath;
            for (String p : path) {
                Nature defaultNature = Nature.n;
                int cut = p.indexOf(' ');
                if (cut > 0) {
                    // 有默认词性
                    String nature = p.substring(cut + 1);
                    p = p.substring(0, cut);
                    try {
                        defaultNature = LexiconUtil.convertStringToNature(nature, customNatureCollector);
                    } catch (Exception e) {
                        LogUtil.logger.severe("配置文件【" + p + "】写错了！" + e);
                        continue;
                    }
                }
                LogUtil.logger.info("以默认词性[" + defaultNature + "]加载自定义词典" + p + "中……");
                boolean success = load(p, defaultNature, map, customNatureCollector);
                if (!success) LogUtil.logger.warning("失败：" + p);
            }
            if (map.size() == 0) {
                LogUtil.logger.warning("没有加载到任何词条");
                map.put(Predefine.TAG_OTHER, null);     // 当作空白占位符
            }
            LogUtil.logger.info("正在构建DoubleArrayTrie……");
            dat.build(map);
            // 缓存成dat文件，下次加载会快很多
            LogUtil.logger.info("正在缓存词典为dat文件……");
            // 缓存值文件
            List<CoreDictionary.Attribute> attributeList = new LinkedList<CoreDictionary.Attribute>();
            for (Map.Entry<String, CoreDictionary.Attribute> entry : map.entrySet()) {
                attributeList.add(entry.getValue());
            }
            DataOutputStream out = new DataOutputStream(IOUtil.newOutputStream(mainPath + Predefine.BIN_EXT));
            // 缓存用户词性
            IOUtil.writeCustomNature(out, customNatureCollector);
            // 缓存正文
            out.writeInt(attributeList.size());
            for (CoreDictionary.Attribute attribute : attributeList) {
                attribute.save(out);
            }
            dat.save(out);
            out.close();
        } catch (FileNotFoundException e) {
            LogUtil.logger.severe("自定义词典" + mainPath + "不存在！" + e);
            return false;
        } catch (IOException e) {
            LogUtil.logger.severe("自定义词典" + mainPath + "读取错误！" + e);
            return false;
        } catch (Exception e) {
            LogUtil.logger.warning("自定义词典" + mainPath + "缓存失败！\n" + TextUtility.exceptionToString(e));
        }
        return true;
    }


    /**
     * 加载用户词典（追加）
     *
     * @param path                  词典路径
     * @param defaultNature         默认词性
     * @param customNatureCollector 收集用户词性
     * @return
     */
    public static boolean load(String path, Nature defaultNature, TreeMap<String, CoreDictionary.Attribute> map, LinkedHashSet<Nature> customNatureCollector) {
        try {
            String splitter = "\\s";
            if (path.endsWith(".csv")) {
                splitter = ",";
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(IOUtil.newInputStream(path), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] param = line.split(splitter);
                if (param[0].length() == 0) continue;   // 排除空行
                if (Config.Normalization) param[0] = CharTable.convert(param[0]); // 正规化

                int natureCount = (param.length - 1) / 2;
                CoreDictionary.Attribute attribute;
                if (natureCount == 0) {
                    attribute = new CoreDictionary.Attribute(defaultNature);
                } else {
                    attribute = new CoreDictionary.Attribute(natureCount);
                    for (int i = 0; i < natureCount; ++i) {
                        attribute.nature[i] = LexiconUtil.convertStringToNature(param[1 + 2 * i], customNatureCollector);
                        attribute.frequency[i] = Integer.parseInt(param[2 + 2 * i]);
                        attribute.totalFrequency += attribute.frequency[i];
                    }
                }
//                if (updateAttributeIfExist(param[0], attribute, map, rewriteTable)) continue;
                map.put(param[0], attribute);
            }
            br.close();
        } catch (Exception e) {
            LogUtil.logger.severe("自定义词典" + path + "读取错误！" + e);
            return false;
        }

        return true;
    }

    /**
     * 如果已经存在该词条,直接更新该词条的属性
     *
     * @param key          词语
     * @param attribute    词语的属性
     * @param map          加载期间的map
     * @param rewriteTable
     * @return 是否更新了
     */
    private static boolean updateAttributeIfExist(String key, CoreDictionary.Attribute attribute, TreeMap<String, CoreDictionary.Attribute> map, TreeMap<Integer, CoreDictionary.Attribute> rewriteTable) {
        int wordID = CoreDictionary.getWordID(key);
        CoreDictionary.Attribute attributeExisted;
        if (wordID != -1) {
            attributeExisted = CoreDictionary.get(wordID);
            attributeExisted.nature = attribute.nature;
            attributeExisted.frequency = attribute.frequency;
            attributeExisted.totalFrequency = attribute.totalFrequency;
            // 收集该覆写
            rewriteTable.put(wordID, attribute);
            return true;
        }

        attributeExisted = map.get(key);
        if (attributeExisted != null) {
            attributeExisted.nature = attribute.nature;
            attributeExisted.frequency = attribute.frequency;
            attributeExisted.totalFrequency = attribute.totalFrequency;
            return true;
        }

        return false;
    }

    /**
     * 往自定义词典中插入一个新词（非覆盖模式）<br>
     * 动态增删不会持久化到词典文件
     *
     * @param word                新词 如“裸婚”
     * @param natureWithFrequency 词性和其对应的频次，比如“nz 1 v 2”，null时表示“nz 1”
     * @return 是否插入成功（失败的原因可能是不覆盖、natureWithFrequency有问题等，后者可以通过调试模式了解原因）
     */
    public static boolean add(String word, String natureWithFrequency) {
        if (contains(word)) return false;
        return insert(word, natureWithFrequency);
    }

    /**
     * 往自定义词典中插入一个新词（非覆盖模式）<br>
     * 动态增删不会持久化到词典文件
     *
     * @param word 新词 如“裸婚”
     * @return 是否插入成功（失败的原因可能是不覆盖等，可以通过调试模式了解原因）
     */
    public static boolean add(String word) {
        if (Config.Normalization) word = CharTable.convert(word);
        if (contains(word)) return false;
        return insert(word, null);
    }

    /**
     * 往自定义词典中插入一个新词（覆盖模式）<br>
     * 动态增删不会持久化到词典文件
     *
     * @param word                新词 如“裸婚”
     * @param natureWithFrequency 词性和其对应的频次，比如“nz 1 v 2”，null时表示“nz 1”。
     * @return 是否插入成功（失败的原因可能是natureWithFrequency问题，可以通过调试模式了解原因）
     */
    public static boolean insert(String word, String natureWithFrequency) {
        if (word == null) return false;
        if (Config.Normalization) word = CharTable.convert(word);
        CoreDictionary.Attribute att = natureWithFrequency == null ? new CoreDictionary.Attribute(Nature.nz, 1) : CoreDictionary.Attribute.create(natureWithFrequency);
        if (att == null) return false;
        if (dat.set(word, att)) return true;
        if (trie == null) trie = new BinTrie<CoreDictionary.Attribute>();
        trie.put(word, att);
        return true;
    }

    /**
     * 以覆盖模式增加新词<br>
     * 动态增删不会持久化到词典文件
     *
     * @param word
     * @return
     */
    public static boolean insert(String word) {
        return insert(word, null);
    }

    /**
     * 从磁盘加载双数组
     *
     * @param path
     * @return
     */
    static boolean loadDat(String path) {
        try {
            ByteArray byteArray = ByteArray.createByteArray(path + Predefine.BIN_EXT);
            if (byteArray == null) return false;
            int size = byteArray.nextInt();
            if (size < 0)   // 一种兼容措施,当size小于零表示文件头部储存了-size个用户词性
            {
                while (++size <= 0) {
                    Nature.create(byteArray.nextString());
                }
                size = byteArray.nextInt();
            }
            CoreDictionary.Attribute[] attributes = new CoreDictionary.Attribute[size];
            final Nature[] natureIndexArray = Nature.values();
            for (int i = 0; i < size; ++i) {
                // 第一个是全部频次，第二个是词性个数
                int currentTotalFrequency = byteArray.nextInt();
                int length = byteArray.nextInt();
                attributes[i] = new CoreDictionary.Attribute(length);
                attributes[i].totalFrequency = currentTotalFrequency;
                for (int j = 0; j < length; ++j) {
                    attributes[i].nature[j] = natureIndexArray[byteArray.nextInt()];
                    attributes[i].frequency[j] = byteArray.nextInt();
                }
            }
            if (!dat.load(byteArray, attributes)) return false;
        } catch (Exception e) {
            LogUtil.logger.warning("读取失败，问题发生在" + TextUtility.exceptionToString(e));
            return false;
        }
        return true;
    }

    /**
     * 查单词
     *
     * @param key
     * @return
     */
    public static CoreDictionary.Attribute get(String key) {
        if (Config.Normalization) key = CharTable.convert(key);
        CoreDictionary.Attribute attribute = dat.get(key);
        if (attribute != null) return attribute;
        if (trie == null) return null;
        return trie.get(key);
    }

    /**
     * 删除单词
     * 动态增删不会持久化到词典文件
     *
     * @param key
     */
    public static void remove(String key) {
        if (Config.Normalization) key = CharTable.convert(key);
        if (trie == null) return;
        trie.remove(key);
    }

    /**
     * 前缀查询
     *
     * @param key
     * @return
     */
    public static LinkedList<Map.Entry<String, CoreDictionary.Attribute>> commonPrefixSearch(String key) {
        return trie.commonPrefixSearchWithValue(key);
    }

    /**
     * 前缀查询
     *
     * @param chars
     * @param begin
     * @return
     */
    public static LinkedList<Map.Entry<String, CoreDictionary.Attribute>> commonPrefixSearch(char[] chars, int begin) {
        return trie.commonPrefixSearchWithValue(chars, begin);
    }

    public static BaseSearcher getSearcher(String text) {
        return new Searcher(text);
    }

    @Override
    public String toString() {
        return "CustomDictionary{" +
                "trie=" + trie +
                '}';
    }

    /**
     * 词典中是否含有词语
     *
     * @param key 词语
     * @return 是否包含
     */
    public static boolean contains(String key) {
        if (dat.exactMatchSearch(key) >= 0) return true;
        return trie != null && trie.containsKey(key);
    }

    /**
     * 获取一个BinTrie的查询工具
     *
     * @param charArray 文本
     * @return 查询者
     */
    public static BaseSearcher getSearcher(char[] charArray) {
        return new Searcher(charArray);
    }

    static class Searcher extends BaseSearcher<CoreDictionary.Attribute> {
        /**
         * 分词从何处开始，这是一个状态
         */
        int begin;

        private LinkedList<Map.Entry<String, CoreDictionary.Attribute>> entryList;

        protected Searcher(char[] c) {
            super(c);
            entryList = new LinkedList<Map.Entry<String, CoreDictionary.Attribute>>();
        }

        protected Searcher(String text) {
            super(text);
            entryList = new LinkedList<Map.Entry<String, CoreDictionary.Attribute>>();
        }

        @Override
        public Map.Entry<String, CoreDictionary.Attribute> next() {
            // 保证首次调用找到一个词语
            while (entryList.size() == 0 && begin < c.length) {
                entryList = trie.commonPrefixSearchWithValue(c, begin);
                ++begin;
            }
            // 之后调用仅在缓存用完的时候调用一次
            if (entryList.size() == 0 && begin < c.length) {
                entryList = trie.commonPrefixSearchWithValue(c, begin);
                ++begin;
            }
            if (entryList.size() == 0) {
                return null;
            }
            Map.Entry<String, CoreDictionary.Attribute> result = entryList.getFirst();
            entryList.removeFirst();
            offset = begin - 1;
            return result;
        }
    }

    /**
     * 获取词典对应的trie树
     *
     * @deprecated
     */
    public static BinTrie<CoreDictionary.Attribute> getTrie() {
        return trie;
    }

    /**
     * 解析一段文本（目前采用了BinTrie+DAT的混合储存形式，此方法可以统一两个数据结构, DAT用于临时存储一些小量数据）
     *
     * @param text      文本
     * @param processor 处理器
     */
    public static void parseText(char[] text, AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute> processor) {
        if (trie != null) {
            trie.parseText(text, processor);
        }
        DoubleArrayTrie<CoreDictionary.Attribute>.Searcher searcher = dat.getSearcher(text, 0);
        while (searcher.next()) {
            processor.hit(searcher.begin, searcher.begin + searcher.length, searcher.value);
        }
    }

    /**
     * 解析一段文本（目前采用了BinTrie+DAT的混合储存形式，此方法可以统一两个数据结构, DAT用于临时存储一些小量数据）
     *
     * @param text      文本
     * @param processor 处理器
     */
    public static void parseText(String text, AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute> processor) {
        if (trie != null) {
            BaseSearcher searcher = CustomDictionary.getSearcher(text);
            int offset;
            Map.Entry<String, CoreDictionary.Attribute> entry;
            while ((entry = searcher.next()) != null) {
                offset = searcher.getOffset();
                processor.hit(offset, offset + entry.getKey().length(), entry.getValue());
            }
        }
        DoubleArrayTrie<CoreDictionary.Attribute>.Searcher searcher = dat.getSearcher(text, 0);
        while (searcher.next()) {
            processor.hit(searcher.begin, searcher.begin + searcher.length, searcher.value);
        }
    }

    /**
     * 重新加载
     * @return 是否加载成功
     */
    public static boolean reload() {
        String path[] = Config.CustomDictionaryPath;
        if (path == null || path.length == 0) return false;
        IOUtil.deleteFile(path[0] + Predefine.BIN_EXT); // 删掉缓存
        return loadMainDictionary(path[0]);
    }
}
