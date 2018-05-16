package com.hankcs.nlp.util;

import com.hankcs.nlp.common.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    /**
     * 日志组件
     */
    public static Logger logger = Logger.getLogger("JBNLP");

    static {
        if (Config.DEBUG) {
            logger.setLevel(Level.INFO);
        } else {
            logger.setLevel(Level.OFF);
        }
    }
}
