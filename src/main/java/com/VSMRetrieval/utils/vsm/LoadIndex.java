package com.VSMRetrieval.utils.vsm;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.lang.reflect.Field;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class LoadIndex {

    private String docsPath;
    private String binPath;

    private TreeMap<String, String>       mapDocs;
    private TreeMap<String, InvertedList> mapTD;
    private TreeMap<String, Double> mapDocDist;


    public TreeMap<String, Double> getMapDocDist() {
        return mapDocDist;
    }
    public void setMapDocDist(TreeMap<String, Double> mapDocDist) {
        this.mapDocDist = mapDocDist;
    }
    public TreeMap<String, String> getMapDocs() {

        try {
            this.getMapDocsSeri();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapDocs;
    }
    public TreeMap<String, InvertedList> getMapTD() {
        return mapTD;
    }
    public void setMapDocs(TreeMap<String, String> mapDocs) {
        this.mapDocs = mapDocs;
    }
    public void setMapTD(TreeMap<String, InvertedList> mapTD) {
        this.mapTD = mapTD;
    }

    public LoadIndex(String docsPath, String binPath) {
        this.docsPath = docsPath;
        this.binPath = binPath;
        this.setMapDocs(new TreeMap<>());
        this.setMapTD(new TreeMap<>());
        this.setMapDocDist(new TreeMap<>());
    }

    private void getMapDocsSeri() throws IOException {

        BufferedReader br      = new BufferedReader(new FileReader(binPath + File.separator + "DOC-Mapping.txt"));
        String         strLine = br.readLine();

        while (strLine != null) {
            String[] split = strLine.split("\t");
            mapDocs.put(split[0], split[1]);
            strLine = br.readLine();
        }

    }

    private void getMapTDSeri() throws  IOException {

        BufferedReader br_inverted = new BufferedReader(new FileReader(binPath + File.separator + "InvertedList.txt"));
        BufferedReader br_docFreq = new BufferedReader(new FileReader(binPath + File.separator + "DocFrequency.txt"));

        String str_inverted = br_inverted.readLine();
        String str_docFreq  = br_docFreq.readLine();

        while (str_inverted != null) {

            String[] split_inverted = str_inverted.split("\t");
            String[] mapTFSplits = split_inverted[1].substring(1, split_inverted[1].length() - 1).split(",");

            String[] split_docFreq = str_docFreq.split("\t");

            TreeMap<String, Integer> mapTF = new TreeMap<>();
            InvertedList invertedList = new InvertedList();

            for (String mapTFSplit:mapTFSplits) {

                String[] s = mapTFSplit.trim().split("=");
                mapTF.put(s[0], Integer.valueOf(s[1]));

            }
            invertedList.setMapTF(mapTF);
            invertedList.setDF(Integer.parseInt(split_docFreq[1]));
            mapTD.put(split_inverted[0], invertedList);

            str_inverted = br_inverted.readLine();
            str_docFreq = br_docFreq.readLine();

        }

    }

    private void getDocsiDF() {

        int N = mapDocs.size();
        mapDocs.forEach((docName, filePath) -> {

            Kryo kryo = new Kryo();
            kryo.register(java.util.TreeMap.class);
            Input input = null;
            try {
                input = new Input(new FileInputStream(binPath + File.separator + "DocTF" + File.separator + docName  + ".dat"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            TreeMap<String, Integer> mapDocTF = (TreeMap<String, Integer>) kryo.readClassAndObject(input);

            final Double[] count = {0.0};
            mapDocTF.forEach((token, tf) -> {

                double iDF = Math.log(N / mapTD.get(token).getDF());
                count[0] += tf * iDF * tf * iDF;
            });

            double sqrt = Math.sqrt(count[0]);
            mapDocDist.put(docName, sqrt);

        });


    }

    public void process() throws IOException {

        this.getMapDocsSeri();
//        this.getMapTDSeri();

        //序列化获取mapTD
        long start = System.nanoTime();
        Kryo kryo = new Kryo();
        kryo.register(java.util.TreeMap.class);
        kryo.register(com.VSMRetrieval.utils.vsm.InvertedList.class);
        Input input = new Input(new FileInputStream(binPath + File.separator + "mapTD.dat"));
        mapTD = (TreeMap<String, InvertedList>) kryo.readClassAndObject(input);
        System.err.println("反序列化耗时：" + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start) + "s");

        start = System.nanoTime();
        this.getDocsiDF();

        Kryo _kryo = new Kryo();
        // 写
        Output output = new Output(new FileOutputStream(binPath + File.separator + "DocDistance.dat"));
        _kryo.register(java.util.TreeMap.class);
        _kryo.writeClassAndObject(output, mapDocDist);
        output.close();
        System.err.println("计算欧几里得耗时：" + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start) + "s");

    }

}
