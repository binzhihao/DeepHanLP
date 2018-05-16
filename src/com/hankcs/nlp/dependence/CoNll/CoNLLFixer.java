package com.hankcs.nlp.dependence.CoNll;

import com.hankcs.nlp.dependence.io.IOUtil;

/**
 * CoNLL是NLP领域著名的评测每年举办NLP领域的赛事。这部分移植用来做一些基准测试。2018.03.01
 * 修正一些非10行的依存语料
 */
public class CoNLLFixer {
    public static boolean fix(String path) {
        StringBuilder sbOut = new StringBuilder();
        for (String line : IOUtil.readLineListWithLessMemory(path)) {
            if (line.trim().length() == 0) {
                sbOut.append(line);
                sbOut.append('\n');
                continue;
            }
            String[] args = line.split("\t");
            for (int i = 10 - args.length; i > 0; --i) {
                line += "\t_";
            }
            sbOut.append(line);
            sbOut.append('\n');
        }
        return IOUtil.saveTxt(path + ".fixed.txt", sbOut.toString());
    }
}
