package com.BSBI;

import com.BSBI.util.Compressor;
import com.BSBI.util.Search;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class TestStanford {

    public static void main(String[] args) {
        Properties props = new Properties();  // set up pipeline properties
        props.put("annotators", "tokenize, ssplit, pos, lemma");   //分词、分句、词性标注和次元信息。
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String          txtWords = "Franklin said, If a man empties his purse into his head,no man can take it away from him,an investment in knowledge always pays the best interest.";  // 待处理文本
        Annotation      document = new Annotation(txtWords);
        pipeline.annotate(document);
        List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap word_temp: words) {
            for (CoreLabel token: word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);   // 获取单词信息
                int pos = token.get(CoreAnnotations.IndexAnnotation.class);
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
                System.out.println(word + " " + pos + " " + lema);
            }
        }
    }

    @Test
    public void testFront() {

//        new Compressor(3).process();
//        String strLine = "barn\taby\t288_72 o\t286_53 sley\t216_45";
//        String front_token = strLine.split("\t")[0];
//        strLine = strLine.substring(front_token.length() + 1, strLine.length());
//        String[] split = strLine.split(" ");
//        for (String info : split) {
//            String token = info.split("\t")[0];
//            String token_info = info.split("\t")[1];
//            System.out.println("token: " + front_token + token);
//            System.out.println("tokenInfo: " + token_info);
//            System.out.println("");
//        }

//        String tokenInfo = "39_367:62_392:194_405_520_53";
//        String[] splitDoc   = tokenInfo.split(":");
//        for (String split :  splitDoc) {
//            String doc =  split.split("_")[0];
//        }

        new Search("french centre-right").process();
    }


}
