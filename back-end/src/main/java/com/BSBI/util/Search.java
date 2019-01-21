package com.BSBI.util;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Search {

    private String                                   query;
    private Map<Integer, String>                     mapQueryPos;
    private String                                   indexPath;
    private Map<String, Map<Integer, List<Integer>>> mapTokenDocPos;
    private Map<String, String>                      mapMatchedInfo;
    private List<Integer>                            docs;
    private List<Integer>                            docResult;

    public Search(String query) {
        this.query = query;
        indexPath = "/home/BSBI/index/compress.txt";
        mapQueryPos = new TreeMap<>();
        mapMatchedInfo = new TreeMap<>();
        mapTokenDocPos = new TreeMap<>();
        docs = new ArrayList<>();
        docResult = new ArrayList<>();
    }

    public List<Integer> process() {
        analyzQuery();
        if (!getIndex()) {
            return null;
        }
        if (!analyzIndex()) {
            return null;
        }
        getResult();

        return docResult;

    }

    private void analyzQuery() {

        // 准备分词器
        Properties props = new Properties();  // set up pipeline properties
        props.put("annotators", "tokenize, ssplit, pos, lemma");   //分词、分句、词性标注和次元信息。
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(query);
        pipeline.annotate(document);
        List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
        int           pos   = 0;
        for (CoreMap word_temp : words) {
            String lema;
            for (CoreLabel token : word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
                lema = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
                mapQueryPos.put(pos++, lema);
            }
        }
    }

    private boolean getIndex() {

        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(indexPath));

            String       strLine;
            List<String> queryTokens = new ArrayList<>(mapQueryPos.values());

            while ((strLine = br.readLine()) != null) {

                if (strLine.equals("")) {
                    continue;
                }
                String front_token = strLine.split("\t")[0];

                // 循环遍历query列表
                for (String querytoken : queryTokens) {
                    // 当前的索引表match查询词
                    if (querytoken.startsWith(front_token)) {
                        String[] splits = strLine.substring(front_token.length() + 1, strLine.length()).split(" ");
                        for (String split : splits) {
                            String token = front_token + split.split("\t")[0];
                            if (querytoken.equals(token)) {
                                mapMatchedInfo.put(querytoken, split.split("\t")[1]);
                            }

                            // 检查是否查询到所有索引
                            if (mapMatchedInfo.size() == mapQueryPos.size()) {
                                return true;
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    private boolean analyzIndex() {

        mapMatchedInfo.forEach((token, tokenInfo) -> {
            String[]                    splitDoc  = tokenInfo.split(":");
            List<Integer>               tempDoc   = new ArrayList<>();
            Map<Integer, List<Integer>> mapDocPos = new TreeMap<>();
            for (String split : splitDoc) {
                String[] docSplit = split.split("_");

                // 当前token的docID
                int docID = Integer.valueOf(docSplit[0]);
                // 当前token的doc中的posList
                List<Integer> posList = new ArrayList<>();

                for (int i = 1; i < docSplit.length; i++) {
                    posList.add(Integer.valueOf(docSplit[i]));
                }

                // 将该token的docID对应posList加入map
                mapDocPos.put(docID, posList);

                tempDoc.add(Integer.valueOf(split.split("_")[0]));
            }

            mapTokenDocPos.put(token, mapDocPos);

            if (docs.size() == 0) {
                docs.addAll(tempDoc);
            } else {
                docs.retainAll(tempDoc);
            }

        });

        if (docs.size() == 0) {
            return false;
        }

        return true;

    }

    private void getResult() {

        List<Integer> queryPosList = new ArrayList<>(mapQueryPos.keySet());

        // 对文档循环
        for (int docId : docs) {

            // 找到第一个token在文档中的所有pos
            List<Integer> posList = mapTokenDocPos.get(mapQueryPos.get(0)).get(docId);
            // 对该pos集合循环
            for (int nowPos : posList) {
                // 对接下来的所有token检索
                int     i    = 1;
                boolean flag = true;
                for (; i < queryPosList.size(); i++) {
                    flag = false;
                    // 获取该token在该docID下的pos
                    List<Integer> integerList = mapTokenDocPos.get(mapQueryPos.get(i)).get(docId);
                    if (integerList.contains(nowPos + i)) {
                        if (i == queryPosList.size() - 1) {
                            flag = true;
                        }
                        continue;
                    } else {
                        break;
                    }
                }

                if (flag) {
                    if (!docResult.contains(docId)) {
                        docResult.add(docId);
                    }
                }

            }


        }

    }

}
