package test;

import com.hankcs.nlp.NLPManager;
import com.hankcs.nlp.extractor.base.AbstractExtractor;
import com.hankcs.nlp.segment.base.AbstractSegment;
import com.hankcs.nlp.segment.common.Term;
import com.hankcs.nlp.common.Config;
import com.hankcs.nlp.util.LogUtil;
import okio.*;

import java.io.File;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 获取分词器
        AbstractSegment segment = NLPManager.getInstance().getSegment();
        // 读取文章
        String text = null;

        boolean isOk = true;
        Source sourceCmd = null, source = null;
        BufferedSource bufferedSourceCmd = null, bufferedSource = null;
        try {
            // 读取文件路径
            System.out.println("请输入测试文章的绝对路径，并确保UTF-8编码：");
            sourceCmd = Okio.source(System.in);
            bufferedSourceCmd = Okio.buffer(sourceCmd);
            String path = bufferedSourceCmd.readUtf8Line();

            // 读取文章
            if (path == null || path.isEmpty()) {
                LogUtil.logger.severe("path is empty");
                return;
            } else {
                source = Okio.source(new File(path));
                bufferedSource = Okio.buffer(source);
                text = bufferedSource.readUtf8();   //一次性读取整篇语料
            }
        } catch (Throwable t) {
            LogUtil.logger.severe(t.toString());
            isOk = false;
        } finally {
            try {
                if (sourceCmd != null) {
                    sourceCmd.close();
                }
                if (source != null) {
                    source.close();
                }
                if (bufferedSourceCmd != null) {
                    bufferedSourceCmd.close();
                }
                if (bufferedSource != null) {
                    bufferedSource.close();
                }
            } catch (Throwable t) {
                LogUtil.logger.severe(t.toString());
            }
        }

        if (!isOk) {
            return;
        }

        if (text != null && !text.isEmpty()) {
            // 分词输出
            List<Term> termList = segment.seg(text.toCharArray());
            LogUtil.logger.info(termList.toString());

            if (Config.APP_DEBUG) {
                // 单独输出分词结果到文件，按照基准测试格式进行输出
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    sink = Okio.sink(new File("D:" + File.separator + "out.txt"));
                    bufferedSink = Okio.buffer(sink);
                    for (Term term : termList) {
                        bufferedSink.writeUtf8(term.word);
                        bufferedSink.writeUtf8("  ");
                    }
                    bufferedSink.flush();   // 最后强制刷新缓冲区一次
                } catch (Throwable t) {
                    LogUtil.logger.severe(t.toString());
                } finally {
                    try {
                        if (sink != null) {
                            sink.close();
                        }
                        if (bufferedSink != null) {
                            bufferedSink.close();
                        }
                    } catch (Throwable t) {
                        LogUtil.logger.severe(t.toString());
                    }
                }
            }

            // 获取关键词提取器
            AbstractExtractor extractor = NLPManager.getInstance().getExtractor();
            // 关键字提取
            System.out.println(extractor.getKeyword(termList));
        }
    }
}
