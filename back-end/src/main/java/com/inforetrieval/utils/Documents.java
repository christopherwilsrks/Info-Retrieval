package com.inforetrieval.utils;

import java.io.*;
import java.util.*;

public class Documents {

    /*存储文件名和文件ID*/
    private Map<Integer, String> mapDoc;
    private int                  totalDoc;
    private int                  totalTokens;
    private int                  totalWords;
    private String               path;
    private String               pathBin;

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public int getTotalDoc() {
        return totalDoc;
    }

    public void setTotalDoc(int totalDoc) {
        this.totalDoc = totalDoc;
    }

    public Documents(String path) throws IOException {
        mapDoc = new TreeMap<Integer, String>();
        this.path = path;
        this.pathBin = path + "/bin";
    }

    public int processDocID() throws IOException {

        File   file     = new File(path);
        File[] tempList = file.listFiles();
        if (tempList == null) {
            return 0;
        }

        File bin = new File(pathBin);
        if (!bin.exists()) {
            bin.mkdir();
        }
        FileWriter     fw = new FileWriter(pathBin + "/Doc-Mapping.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        int i = 0, j = 0;
        for (; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String docname = tempList[i].getName();
                System.out.println("DOCUMENTS [" + j + "]:" + docname);
                mapDoc.put(j, tempList[i].getName());
                bw.write(String.format("%d %s\r\n", j, docname));
                j++;
            }
        }
        bw.flush();
        bw.close();

        setTotalDoc(i + 1);
        return 1;
    }

    public void seperate() throws IOException {

        for (int i = 0; i < mapDoc.size(); i++) {
            FileReader     fr       = new FileReader("./documents/" + mapDoc.get(i));
            BufferedReader br       = new BufferedReader(fr);
            String         strLine  = br.readLine();
            String         strWrite = "";

            FileWriter     fw = null;
            BufferedWriter bw = null;

            while (strLine != null) {
                strWrite += strLine + "\n\r";

                if (strLine.contains("<DOCNO>")) {
                    strLine = strLine.replaceAll("<DOCNO>", "");
                    strLine = strLine.replaceAll("</DOCNO>", "");
                    fw = new FileWriter("./documents/" + strLine + ".txt");
                    bw = new BufferedWriter(fw);
                }
                if (strLine.equals("</DOC>")) {
                    bw.write(strWrite);
                    bw.flush();
                    bw.close();
                    fw.close();
                    strWrite = "";
                }
                strLine = br.readLine();
            }
            br.close();
            fr.close();
        }

    }

    public int docReading() throws IOException {

        List<InversedList>   inversedLists = new ArrayList<InversedList>();
        Map<String, Integer> mapTokens     = new HashMap<String, Integer>();

        for (int i = 0; i < mapDoc.size(); i++) {
            String         document = path + "/" + mapDoc.get(0);
            FileReader     fr       = new FileReader(path + File.separator + mapDoc.get(i));
            BufferedReader br       = new BufferedReader(fr);
            String         strLine  = br.readLine();

            while (strLine != null) {

                String strTemp = strLine.replaceAll("[\t|\n|\r]", " ").trim();
                strTemp = strTemp.replaceAll("[^a-zA-Z]", " ");
                processLine(strTemp, i, inversedLists, mapTokens);
                strLine = br.readLine();
            }
            br.close();
            fr.close();
        }
        Collections.sort(inversedLists, new Comparator<InversedList>() {
            public int compare(InversedList o1, InversedList o2) {
                return o1.getToken().compareTo(o2.getToken());
            }
        });

        //write to file
        this.writeToFile(inversedLists,this.kGram(inversedLists));
        return 1;
    }

    private Map<String, ArrayList<String>> kGram(List<InversedList> inversedLists) {

        /*2-gram对应token的树*/
        Map<String, ArrayList<String>> mapK_gram = new TreeMap<String, ArrayList<String>>();

        for (InversedList inversedList : inversedLists) {

            String token = inversedList.getToken();
            token = "$" + token + "$";

            /*将该token的所有2-gram存入mapTemp中，确保该token的2-gram不重复*/
            Map<Integer, String> mapTemp = new TreeMap<Integer, String>();
            for (int i = 0; i < token.length() - 1; i++) {

                mapTemp.put(i, token.substring(i, i + 2));

            }

            token = token.substring(1, token.length() - 1);
            for (int j = 0; j < mapTemp.size(); j++) {

                String gram = mapTemp.get(j);
                if (mapK_gram.containsKey(gram)) {

                    ArrayList<String> tokenGram = mapK_gram.get(gram);
                    tokenGram.add(token);
                    mapK_gram.put(gram, tokenGram);

                }
                else {

                    ArrayList<String> t = new ArrayList<String>();
                    t.add(token);
                    mapK_gram.put(gram, t);

                }

            }


        }

        return mapK_gram;

    }

    private void processLine(String strLine, int docID, List<InversedList> inversedLists, Map<String, Integer> mapTokens) {

        String str[] = strLine.split(" ");
        for (String item : str) {
            if (item == null || item.trim().length() == 0) {
                continue;
            }
            item = item.trim();
            if (!mapTokens.containsKey(item.toLowerCase())) {
                mapTokens.put(item.toLowerCase(), mapTokens.size());
                InversedList inversedList = new InversedList();
                inversedList.addDocIDtoList(docID);
                inversedList.setToken(item.toLowerCase());
                inversedList.setTokenID(mapTokens.size() - 1);
                inversedList.addToMapCount(docID);
                inversedLists.add(inversedList);
                this.setTotalTokens(this.getTotalTokens() + 1);
            } else {
                Integer itemValue = mapTokens.get(item.toLowerCase());
                inversedLists.get(itemValue).addDocIDtoList(docID);
                inversedLists.get(itemValue).addToMapCount(docID);
            }
            this.setTotalWords(this.getTotalWords() + 1);
        }

    }

    private void writeToFile(List<InversedList> inversedLists, Map<String, ArrayList<String>> mapK_gram) throws IOException {

        FileWriter     fw         = new FileWriter(pathBin + "/InversedList.txt");
        BufferedWriter bw_inverse = new BufferedWriter(fw);
        FileWriter     fw2        = new FileWriter(pathBin+ "/2-grams.txt");
        BufferedWriter bw_grams  = new BufferedWriter(fw2);
        FileWriter     fw3      = new FileWriter(pathBin + "/DocIDs.txt");
        BufferedWriter bw_docID = new BufferedWriter(fw3);

        for (int i = 0; i < totalTokens; i++) {

            InversedList inversedList = inversedLists.get(i);

            String                strInversed        = inversedList.getToken() + " " + inversedList.getTokenID() + " " + inversedList.getCount() + " ";
            Map<Integer, Integer> mapTokeninDocCount = inversedList.getMapTokeninDocCount();
            String strDocIDs = String.valueOf(inversedList.getTokenID()) + " ";
            for (int docID : inversedList.getListDocID()) {
                strDocIDs += String.format("%d ", docID);
                strInversed += mapTokeninDocCount.get(docID) + " ";
            }
            strInversed += "\r\n";
            strDocIDs += "\r\n";

            bw_docID.write(strDocIDs);
            bw_inverse.write(strInversed);
        }

        for (Map.Entry<String, ArrayList<String>> entry:mapK_gram.entrySet()) {

            String strK_gram = entry.getKey();

            ArrayList<String> tokenGram = entry.getValue();
            for (String token:tokenGram) {

                strK_gram += " " + token;

            }
            strK_gram +="\r\n";
            bw_grams.write(strK_gram);

        }

        bw_docID.flush();
        bw_inverse.flush();
        bw_docID.close();
        bw_inverse.close();
        bw_grams.flush();
        bw_grams.close();
        fw.close();
        fw2.close();
        fw3.close();

    }

}
