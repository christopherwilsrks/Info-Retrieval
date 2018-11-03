package com.VSMRetrieval.utils.vsm;

import com.VSMRetrieval.utils.nlp.StanfordNLP;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MakeIndex {

    private String docsPath;
    private String binPath;

    private TreeMap<String, String> mapDocs;

    public MakeIndex(String docsPath, String binPath) {
        this.docsPath = docsPath;
        this.binPath = binPath;
        this.setMapDocs(new TreeMap<>());
    }

    public void setMapDocs(TreeMap<String, String> mapDocs) {
        this.mapDocs = mapDocs;
    }

    private int getDocLength(String filePath) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String strLine = br.readLine();

        int docLength = 0;

        while (strLine != null) {
            docLength += new StanfordNLP().getNPL(strLine).size();
            strLine = br.readLine();
        }

        return docLength;

    }

    private void getDocsTF(String fileName, ArrayList<String> tokens) throws FileNotFoundException {





    }

    private TreeMap<String, InvertedList> processTokens(ArrayList<String> tokens, String fileName, TreeMap<String, InvertedList> mapTD, TreeMap<String, Integer> mapDocTF) {

        for (String token:tokens) {

            token = token.toLowerCase();

            if (token.replaceAll("[^a-zA-Z]", "").length() == 0) {
                continue;
            }

            if (!mapDocTF.containsKey(token)) {
                mapDocTF.put(token, 1);
            } else {
                mapDocTF.put(token, mapDocTF.get(token) + 1);
            }

            if (!mapTD.containsKey(token)) {

                TreeMap<String, Integer> mapTF = new TreeMap<>();
                mapTF.put(fileName, 1);
                InvertedList invertedList = new InvertedList();
                invertedList.setMapTF(mapTF);
                invertedList.setDF(1);
                mapTD.put(token, invertedList);
            } else {
                InvertedList             invertedList = mapTD.get(token);
                TreeMap<String, Integer> mapTF        = invertedList.getMapTF();

                if (!mapTF.containsKey(fileName)) {
                    invertedList.setDF(invertedList.getDF() + 1);
                    mapTF.put(fileName, 1);
                } else {
                    mapTF.put(fileName, mapTF.get(fileName) + 1);
                }

                invertedList.setMapTF(mapTF);
                mapTD.put(token, invertedList);

            }

        }

        return mapTD;

    }

    private void writeToDocMapping() throws IOException {

        File dir = new File(docsPath);
        File[] files = dir.listFiles();

        int index = 0;

        BufferedWriter bw_docs  = new BufferedWriter(new FileWriter(binPath + File.separator +  "DOC-Mapping.txt"));

        for (File file:files) {
            String         filePath = file.getAbsolutePath();
            int docLength = this.getDocLength(filePath);

            mapDocs.put("DOC_" + index, filePath);

            bw_docs.write("DOC_" + index++ + "\t" + filePath + "\t" + docLength);
            bw_docs.newLine();
            bw_docs.flush();
        }

        bw_docs.close();

    }

    private void getInvertedList(TreeMap<String, InvertedList> mapTD) throws IOException {


        mapDocs.forEach((fileName, filePath) -> {

            TreeMap<String, Integer> mapDocTF = new TreeMap<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));

                String strLine = br.readLine();
                while (strLine != null) {

                    ArrayList<String> tokens = new StanfordNLP().getNPL(strLine);
                    this.processTokens(tokens, fileName, mapTD, mapDocTF);
                    strLine = br.readLine();

                }

                Kryo kryo = new Kryo();
                // 写
                long start = System.nanoTime();
                Output    output     = new Output(new FileOutputStream(binPath + File.separator + "DocTF" + File.separator + fileName + ".dat"));
                kryo.register(java.util.TreeMap.class);
                kryo.writeClassAndObject(output, mapDocTF);
                output.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    private void writeToInvertedList(TreeMap<String, InvertedList> mapTD) throws IOException {

        BufferedWriter bw_inverted = new BufferedWriter(new FileWriter(binPath + File.separator + "InvertedList.txt"));
        BufferedWriter bw_docFreq = new BufferedWriter(new FileWriter(binPath + File.separator + "DocFrequency.txt"));

        mapTD.forEach((token, invertedList) -> {

            try {
                bw_inverted.write(token + "\t" + invertedList.getMapTF().toString());
                bw_inverted.newLine();
                bw_inverted.flush();

                bw_docFreq.write(token + "\t" + invertedList.getDF());
                bw_docFreq.newLine();
                bw_docFreq.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        bw_inverted.close();
        bw_docFreq.close();

    }

    public void process() throws IOException {

        TreeMap<String, InvertedList> mapTD = new TreeMap<>();

        this.writeToDocMapping();
        this.getInvertedList(mapTD);
        this.writeToInvertedList(mapTD);

        Kryo kryo = new Kryo();
        // 写
        long start = System.nanoTime();
        Output    output     = new Output(new FileOutputStream(binPath + File.separator + "mapTD.dat"));
        kryo.register(java.util.TreeMap.class);
        kryo.register(com.VSMRetrieval.utils.vsm.InvertedList.class);
        kryo.writeClassAndObject(output, mapTD);
        output.close();

        System.err.println("序列化耗时：" + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start) + "s");

    }

}
