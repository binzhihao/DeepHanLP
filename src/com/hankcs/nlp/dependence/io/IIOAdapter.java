package com.hankcs.nlp.dependence.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO适配器接口
 * 实现该接口可以将代码移植到不同的平台。后续用 Okio 实现
 *
 * @author hankcs
 */
public interface IIOAdapter {
    /**
     * 打开一个文件以供读取
     *
     * @param path 文件路径
     * @return 一个输入流
     * @throws IOException 任何可能的IO异常
     */
    InputStream open(String path) throws IOException;

    /**
     * 创建一个新文件以供输出
     *
     * @param path 文件路径
     * @return 一个输出流
     * @throws IOException 任何可能的IO异常
     */
    OutputStream create(String path) throws IOException;
}
