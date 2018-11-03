package com.VSMRetrieval.utils.vsm;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import sun.reflect.generics.tree.Tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Query {

    private String[] query;
    private String   docsPath;
    private String   binPath;

    //记录每个文档对应查询词项的tf
    private TreeMap<String, TreeMap<String, Double>> mapVector;
    private TreeMap<String, String> mapDocs;

    public TreeMap<String, String> getMapDocs() {
        return mapDocs;
    }
    public void setMapDocs(TreeMap<String, String> mapDocs) {
        this.mapDocs = mapDocs;
    }
    public TreeMap<String, TreeMap<String, Double>> getMapVector() {
        return mapVector;
    }
    public void setMapVector(TreeMap<String, TreeMap<String, Double>> mapVector) {
        this.mapVector = mapVector;
    }

    public Query(String query, String docsPath, String binPath) {
        this.query = query.toLowerCase().split("\\W");
        this.setMapVector(new TreeMap<>());
        this.docsPath = docsPath;
        this.binPath = binPath;
        this.setMapDocs(new TreeMap<>());
    }

    public List<Map.Entry<String,Double>> process() {

        try {
            LoadIndex loadIndex = new LoadIndex(docsPath, binPath);
            loadIndex.process();
            mapDocs = loadIndex.getMapDocs();
            final TreeMap<String, InvertedList> mapTD = loadIndex.getMapTD();

            int N = mapDocs.size();

            HashMap<String, Double> mapQuery = new HashMap<>();

            for (String word : query) {

                if (!mapQuery.containsKey(word)) {
                    mapQuery.put(word, 1.0);
                } else {
                    mapQuery.put(word, mapQuery.get(word) + 1);
                }
            }

            //计算查询语句的tf-idf，如果计算得tf-idf为0，删除该词
            for (Iterator<Map.Entry<String, Double>> it = mapQuery.entrySet().iterator(); it.hasNext(); ) {

                Map.Entry<String, Double> next = it.next();

                String word = next.getKey();
                Double tf   = next.getValue();

                InvertedList invertedList = mapTD.get(word);
                if (invertedList != null) {
                    double iDF = Math.log(N / invertedList.getDF());
                    if (iDF == 0) {
                        it.remove();
                        continue;
                    }
                    mapQuery.put(word, tf * iDF);
                } else {
                    it.remove();
                }

            }

            TreeMap<String, Double> cosineSimilarity = new TreeMap<>();
            //下面对所有文档进行对应tf-idf计算
            mapQuery.forEach((word, tfidf) -> {

                //对每一个查询词项，找到该词项的tf和df
                InvertedList             invertedList = mapTD.get(word);
                TreeMap<String, Integer> mapTF        = invertedList.getMapTF();

                double iDF = Math.log(N / invertedList.getDF());

                mapTF.forEach((docName, tf) -> {

                    TreeMap<String, Double> mapWeight = new TreeMap<>();

                    if (!mapVector.containsKey(docName)) {
                        mapWeight.put(word, Double.valueOf(tf));
                        cosineSimilarity.put(docName, tf * iDF * tfidf);
                        mapVector.put(docName, mapWeight);
                    } else {
                        mapWeight = mapVector.get(docName);
                        cosineSimilarity.put(docName, cosineSimilarity.get(docName) + tf * iDF * tfidf);
                        mapWeight.put(word, Double.valueOf(tf));
                        mapVector.put(docName, mapWeight);

                    }

                });


            });

            Kryo kryo = new Kryo();
            kryo.register(java.util.TreeMap.class);
            Input input = new Input(new FileInputStream(binPath + File.separator + "DocDistance.dat"));
            TreeMap<String, Double> mapDocDist = (TreeMap<String, Double>) kryo.readClassAndObject(input);

            mapVector.forEach((docName, mapWeight) -> {

                cosineSimilarity.put(docName, cosineSimilarity.get(docName)/mapDocDist.get(docName));

            });

            List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(cosineSimilarity.entrySet());

            //升序排序
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

            System.out.println("---");

            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
