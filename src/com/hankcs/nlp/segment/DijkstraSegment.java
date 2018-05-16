package com.hankcs.nlp.segment;

import com.hankcs.nlp.segment.base.WordBasedSegment;
import com.hankcs.nlp.segment.common.*;
import com.hankcs.nlp.segment.recognition.nr.PersonRecognition;
import com.hankcs.nlp.segment.recognition.nr.TranslatedPersonRecognition;
import com.hankcs.nlp.segment.recognition.ns.PlaceRecognition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 最简单的最短路径分词
 */
public class DijkstraSegment extends WordBasedSegment {

    @Override
    public List<Term> segSentence(char[] sentence) {

        WordNet wordNetOptimum = new WordNet(sentence);
        WordNet wordNetAll = new WordNet(wordNetOptimum.charArray);

        GenerateWordNet(wordNetAll);

        Graph graph = GenerateBiGraph(wordNetAll);

        List<Vertex> vertexList = dijkstra(graph);

        // 实体命名识别
        if (config.ner) {
            wordNetOptimum.addAll(vertexList);
            int preSize = wordNetOptimum.size();
            if (config.nameRecognize) {
                PersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.translatedNameRecognize) {
                TranslatedPersonRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.placeRecognize) {
                PlaceRecognition.Recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (wordNetOptimum.size() != preSize) {
                graph = GenerateBiGraph(wordNetOptimum);
                vertexList = dijkstra(graph);
            }
        }

        // 是否标注词性
        if (config.speechTagging) {
            speechTagging(vertexList);
        }

        return convert(vertexList);
    }

    private static List<Vertex> dijkstra(Graph graph) {
        List<Vertex> resultList = new LinkedList<Vertex>();
        Vertex[] vertexes = graph.getVertexes();
        List<EdgeFrom>[] edgesTo = graph.getEdgesTo();
        double[] d = new double[vertexes.length];
        Arrays.fill(d, Double.MAX_VALUE);
        d[d.length - 1] = 0;
        int[] path = new int[vertexes.length];
        Arrays.fill(path, -1);
        PriorityQueue<State> que = new PriorityQueue<State>();
        que.add(new State(0, vertexes.length - 1));
        while (!que.isEmpty()) {
            State p = que.poll();
            if (d[p.vertex] < p.cost) continue;
            for (EdgeFrom edgeFrom : edgesTo[p.vertex]) {
                if (d[edgeFrom.from] > d[p.vertex] + edgeFrom.weight) {
                    d[edgeFrom.from] = d[p.vertex] + edgeFrom.weight;
                    que.add(new State(d[edgeFrom.from], edgeFrom.from));
                    path[edgeFrom.from] = p.vertex;
                }
            }
        }
        for (int t = 0; t != -1; t = path[t]) {
            resultList.add(vertexes[t]);
        }
        return resultList;
    }

}
