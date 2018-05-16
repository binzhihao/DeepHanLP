package com.hankcs.nlp.dependence.CoNll;

/**
 * CoNLL是NLP领域著名的评测每年举办NLP领域的赛事。这部分移植用来做一些基准测试。2018.03.01
 *
 * @author hankcs
 */
public class CoNLLWord {
    /**
     * ID	当前词在句子中的序号，１开始.
     */
    public int ID;
    /**
     * 当前词语（或标点）的原型或词干，在中文中，此列与FORM相同
     */
    public String LEMMA;
    /**
     * 当前词语的词性（粗粒度）
     */
    public String CPOSTAG;
    /**
     * 当前词语的词性（细粒度）
     */
    public String POSTAG;
    /**
     * 当前词语的中心词
     */
    public CoNLLWord HEAD;
    /**
     * 当前词语与中心词的依存关系
     */
    public String DEPREL;

    /**
     * 等效字符串
     */
    public String NAME;

    /**
     * 根节点
     */
    public static final CoNLLWord ROOT = new CoNLLWord(0, "##核心##", "ROOT", "root");
    /**
     * 空白节点，用于描述下标超出word数组的词语
     */
    public static final CoNLLWord NULL = new CoNLLWord(-1, "##空白##", "NULL", "null");

    /**
     * @param ID     当前词在句子中的序号，１开始.
     * @param LEMMA  当前词语（或标点）的原型或词干，在中文中，此列与FORM相同
     * @param POSTAG 当前词语的词性（细粒度）
     */
    public CoNLLWord(int ID, String LEMMA, String POSTAG) {
        this.ID = ID;
        this.LEMMA = LEMMA;
        this.CPOSTAG = POSTAG.substring(0, 1);   // 取首字母作为粗粒度词性
        this.POSTAG = POSTAG;
        compile();
    }

    /**
     * @param ID      当前词在句子中的序号，１开始.
     * @param LEMMA   当前词语（或标点）的原型或词干，在中文中，此列与FORM相同
     * @param CPOSTAG 当前词语的词性（粗粒度）
     * @param POSTAG  当前词语的词性（细粒度）
     */
    public CoNLLWord(int ID, String LEMMA, String CPOSTAG, String POSTAG) {
        this.ID = ID;
        this.LEMMA = LEMMA;
        this.CPOSTAG = CPOSTAG;
        this.POSTAG = POSTAG;
        compile();
    }

    private void compile() {
        this.NAME = PosTagCompiler.compile(POSTAG, LEMMA);
    }

    public CoNLLWord(CoNllLine line) {
        LEMMA = line.value[2];
        CPOSTAG = line.value[3];
        POSTAG = line.value[4];
        DEPREL = line.value[7];
        ID = line.id;
        compile();
    }

    public CoNLLWord(CoNllLine[] lineArray, int index) {
        this(lineArray[index]);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        // ID为0时为根节点，ID为-1时为空白节点
        if (ID != 0 && ID != -1) {
            sb.append(ID).append('\t').append(LEMMA).append('\t').append(LEMMA).append('\t').append(CPOSTAG).append('\t')
                    .append(POSTAG).append('\t').append('_').append('\t').append(HEAD.ID).append('\t').append(DEPREL).append('\t')
                    .append('_').append('\t').append('_');
        } else {
            sb.append(ID).append('\t').append(LEMMA).append('\t').append(LEMMA).append('\t').append(CPOSTAG).append('\t')
                    .append(POSTAG).append('\t').append('_').append('\t').append('_').append('\t').append(DEPREL).append('\t')
                    .append('_').append('\t').append('_');
        }
        return sb.toString();
    }
}
