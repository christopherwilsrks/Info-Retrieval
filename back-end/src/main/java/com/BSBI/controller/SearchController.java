package com.BSBI.controller;


import com.BSBI.util.Search;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/4")
public class SearchController {

    @RequestMapping(value = "/search")
    @ResponseBody
    public List<Integer> search(String query) {

        List<Integer> docResult = new Search(query).process();

        if (docResult.size() == 0) {
            return null;
        }

        return docResult;

    }

    @RequestMapping("/searchDoc")
    @ResponseBody
    public String searchDoc(int docID, String s) throws IOException {

        List<String> tokens = new ArrayList<>();

        Properties props = new Properties();  // set up pipeline properties
        props.put("annotators", "tokenize, ssplit, pos, lemma");   //分词、分句、词性标注和次元信息。
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation      document = new Annotation(s);
        pipeline.annotate(document);
        List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap word_temp: words) {
            for (CoreLabel token: word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
                tokens.add(lema);
            }
        }

        BufferedReader bw = new BufferedReader(new FileReader("/home/BSBI/docs" + File.separator + docID + ".txt"));

        String strLine = bw.readLine();
        String passage = "";

        while(strLine != null) {

            for (String token: tokens) {

                if (strLine.toLowerCase().contains(token)) {
                    strLine = strLine.replaceAll("(?i)" + token, "<font color=\"red\"><i>" + token.toUpperCase() + "</i></font>");
                }

            }

            passage += "<p>" + strLine + "</p>\n";
            strLine = bw.readLine();

        }

        return passage;
    }

    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        String dir  = "/home/BSBI/download/README.zip";
        File   file = new File(dir);

        if (file.exists()) {
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            try {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-disposition", "attachment; filename=" + new String("README.zip".getBytes("utf-8"), "ISO8859-1")); // 指定下载的文件名
                os.write(FileUtils.readFileToByteArray(file));
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }
    }

}
