package com.hankcs.nlp.extractor;

import com.hankcs.nlp.collection.heap.MaxHeap;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.extractor.base.AbstractExtractor;

import java.util.*;

public class TextRankExtractor extends AbstractExtractor {

    private static final int MAX = 200; // 最大迭代次数
    private static final float FACTOR = 0.85f;    //DampingFactor
    private static final float DELTA = 0.001f;    // 误差
    private static final int COUNT = 5; // 默认的关键词提取数

    @Override
    public List<String> getKeyword(List<Term> termList) {
        return getKeyword(termList, COUNT);
    }

    @Override
    public List<String> getKeyword(List<Term> termList, int count) {
        Set<Map.Entry<String, Float>> entrySet = getTermAndRank(termList, count).entrySet();
        List<String> result = new ArrayList<>(entrySet.size());
        for (Map.Entry<String, Float> entry : entrySet) {
            result.add(entry.getKey());
        }
        return result;
    }

    private Map<String, Float> getTermAndRank(List<Term> termList, int count) {
        Map<String, Float> map = getRank(termList);
        Map<String, Float> result = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : new MaxHeap<>(count, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 真正提取关键词的方法
     *
     * @param termList 分词列表
     * @return 关键词以及排名
     */
    private Map<String, Float> getRank(List<Term> termList) {
        List<String> wordList = new ArrayList<>(termList.size());
        for (Term t : termList) {
            // 按照规则过滤掉一些词语
            if (shouldInclude(t)) {
                wordList.add(t.word);
            }
        }
        Map<String, Set<String>> words = new TreeMap<>();
        Queue<String> que = new LinkedList<>();
        for (String w : wordList) {
            if (!words.containsKey(w)) {
                words.put(w, new TreeSet<>());
            }
            // 复杂度O(n-1)
            if (que.size() >= 5) {
                que.poll();
            }
            for (String qWord : que) {
                if (w.equals(qWord)) {
                    continue;
                }
                //既然是邻居,那么关系是相互的,遍历一遍即可
                words.get(w).add(qWord);
                words.get(qWord).add(w);
            }
            que.offer(w);
        }
        Map<String, Float> score = new HashMap<>();
        for (int i = 0; i < MAX; ++i) {
            Map<String, Float> m = new HashMap<>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet()) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - FACTOR);
                for (String element : value) {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0) continue;
                    m.put(key, m.get(key) + FACTOR / size * (score.get(element) == null ? 0 : score.get(element)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= DELTA) {
                break;
            }
        }

        return score;
    }
}
