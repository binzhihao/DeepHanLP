package com.hankcs.nlp.util;

import com.hankcs.nlp.dictionary.base.tag.Nature;
import com.hankcs.nlp.segment.common.Vertex;
import com.hankcs.nlp.segment.recognition.nr.PersonRecognition;
import com.hankcs.nlp.segment.recognition.nt.OrganizationRecognition;
import com.hankcs.nlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import com.hankcs.nlp.dictionary.CustomDictionary;

import java.util.Map;
import java.util.TreeMap;

import static com.hankcs.nlp.util.LogUtil.logger;

/**
 * 运行时动态增加词性工具
 */
public class CustomNatureUtil {
    static {
        logger.warning("已激活自定义词性功能,由于采用了反射技术,用户需对本地环境的兼容性和稳定性负责!\n" +
                "如果用户代码X.java中有switch(nature)语句,需要调用CustomNatureUtility.registerSwitchClass(X.class)注册X这个类");
    }

    private static Map<String, Nature> extraValueMap = new TreeMap<String, Nature>();

    /**
     * 动态增加词性工具
     */
    private static EnumBuster<Nature> enumBuster = new EnumBuster<Nature>(Nature.class,
            CustomDictionary.class,
            Vertex.class,
            PersonRecognition.class,
            OrganizationRecognition.class);

    /**
     * 增加词性
     *
     * @param name 词性名称
     * @return 词性
     */
    public static Nature addNature(String name) {
        Nature customNature = extraValueMap.get(name);
        if (customNature != null) return customNature;
        customNature = enumBuster.make(name);
        enumBuster.addByValue(customNature);
        extraValueMap.put(name, customNature);
        // 必须对词性标注HMM模型中的元组做出调整
        CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary.extendSize();

        return customNature;
    }

    /**
     * 注册switch(nature)语句类
     *
     * @param switchUsers 任何使用了switch(nature)语句的类
     */
    public static void registerSwitchClass(Class... switchUsers) {
        enumBuster.registerSwitchClass(switchUsers);
    }

    /**
     * 还原对词性的全部修改
     */
    public static void restore() {
        enumBuster.restore();
        extraValueMap.clear();
    }

    public static Nature getNature(String name) {

        try {
            return Nature.valueOf(name);
        } catch (Exception e) {
            // 动态添加的词语有可能无法通过valueOf获取
            return extraValueMap.get(name);
        }
    }
}
