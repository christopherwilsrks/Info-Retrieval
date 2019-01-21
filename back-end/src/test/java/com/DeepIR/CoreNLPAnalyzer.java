package com.DeepIR;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.lucene.analysis.Analyzer;

import java.io.Reader;

public class CoreNLPAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s, Reader reader) {
        return new TokenStreamComponents(new CoreNLPTokenizer(reader, new StanfordCoreNLP("StanfordCoreNLP-Chinese.properties")));
    }
}
