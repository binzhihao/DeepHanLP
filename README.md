# 项目结构
* algorithm ---- 一些通用算法的实现
* collection ---- 一些通用数据结构的实现
* common ---- 一些通用配置
* dependence ---- 从第三方移植或直接拿过来用的依赖项
* dictionary ---- 词典相关，主要是词典基础数据结构的定义以及词典数据的加载和解析
* extractor ---- 关键词提取相关
* model ---- 高阶算法的模型定义和训练代码，目前包含最大熵模型和trigram模型，CRF效果欠佳暂不考虑，相关理论参考论文
* pool ---- 对象缓存池，高并发下对只读对象复用，后续拓展
* segment ---- 分词相关，包含低阶到高阶多种实现，相关理论参考论文
* semantic ---- 语义相关
* test ---- 测试主程序入口，所有调用统一放到这里
* util ---- 抽离出来的全局工具类
* NLPManager ---- 此程序唯一的调用入口，建议遵循此设计模式，保持这里的对外接口不变，内部实现可随意调整

# 使用
参考 Demo.java 的调用方式 以及 NLPManager.java 的公开函数

## 中文分词
```java
// Step0. 读取文本(UTF-8，对齐 CoNLL 测试)
String text;
// Step1. 获取分词器
AbstractSegment segment = NLPManager.getInstance().getSegment();
// Step2. 执行分词
List<Term> termList = segment.seg(text.toCharArray());
```
## 关键字提取
```java
// Step1. 获取关键词提取器
AbstractExtractor extractor = NLPManager.getInstance().getExtractor();
// Step2. 关键字提取
List<String> keyWords = extractor.getKeyword(termList);
```

# Reference
[1] http://sighan.cs.uchicago.edu/bakeoff2005/

[2] http://www.signll.org/conll

[3] https://monkeylearn.com/blog/word-embeddings-transform-text-numbers/

[4] https://tryolabs.com/blog/2017/12/12/deep-learning-for-nlp-advancements-and-trends-in-2017/

[5] http://www.thunlp.org/~lzy/publications/phd_slides.pdf

[6] Vogel S, Ney H, Tillmann C. HMM-based word alignment in statistical translation[J]. Coling, 1996, 9(1):836-841.

[7] Le N H N, Quoc H B. Integrating Low-rank Approximation and Word Embedding for Feature Transformation in the High-dimensional Text Classification[J]. Procedia Computer Science, 2017, 112:437-446.

[8] From Characters to Understanding Natural Language (C2NLU): Robust End-to-End Deep Learning for NLP Phil Blunsom, Kyunghyun Cho, Chris Dyer and Hinrich Schütze (2017)

# License
Apache 2.0