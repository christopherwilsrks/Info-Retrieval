package com.BSBI.util;

import java.io.*;
import java.util.*;

public class BSBIIndex {

    // 文档唯一标识ID
    public static int DOC_ID = 0;

    // 读缓冲区的大小
    private int                 readBufferSize;
    // 写缓冲区的大小
    private int                 writeBufferSize;
    // 读入的文档的有效词文件地址
    private ArrayList<String>   effectiveWordFiles;
    // 倒排索引输出文件地址
    private String              outputFilePath;
    // 读缓冲 1
    private String[][]          readBuffer1;
    // 读缓冲2
    private String[][]          readBuffer2;
    // 写缓冲区
    private String[][]          writeBuffer;
    // 有效词与hashcode的映射
    private Map<String, String> code2word;

    public BSBIIndex(ArrayList<String> effectiveWordFiles, int readBufferSize,
                     int writeBufferSize) {
        this.effectiveWordFiles = effectiveWordFiles;
        this.readBufferSize = readBufferSize;
        this.writeBufferSize = writeBufferSize;

        initBuffers();
    }

    /**
     * 初始化缓冲区的设置
     */
    private void initBuffers() {
        readBuffer1 = new String[readBufferSize][2];
        readBuffer2 = new String[readBufferSize][2];
        writeBuffer = new String[writeBufferSize][2];
    }

    /**
     * 从文件中读取有效词并进行编码替换
     *
     * @param filePath 返回文档
     */
    private Document readEffectWords(String filePath) {
        long hashcode = 0;

        String   w;
        Document document;
        code2word = new HashMap<String, String>();
        Map<String, List<Integer>> words;

        words = readDataFile(filePath);

        int docID = Integer.parseInt(filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('-')));

        document = new Document(words, filePath, docID);

        return document;
    }

    /**
     * 根据输入的有效词输出倒排索引文件
     */
    public void outputInvertedFiles() {
        int    index        = 0;
        String baseFilePath = "";
        outputFilePath = "";
        Document            doc;
        Map<String, String> allDoc;
        ArrayList<String>   tempPaths;
        ArrayList<String[]> invertedData1;
        ArrayList<String[]> invertedData2;

        tempPaths = new ArrayList<>();
        for (int i = 0; i < effectiveWordFiles.size(); i++) {
            doc = readEffectWords(effectiveWordFiles.get(i));
            allDoc = readBsbiDoc();
            if (i == 0) {
                writeOutOperation(doc, null);
            } else {
                writeOutOperation(doc, allDoc);
            }

        }
    }


    /**
     * 从文件中读取数据
     *
     * @param filePath 单个文件
     */
    private Map<String, List<Integer>> readDataFile(String filePath) {
        File                       file      = new File(filePath);
        ArrayList<String[]>        dataArray = new ArrayList<String[]>();
        Map<String, List<Integer>> words     = new TreeMap<>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String         str;
            String[]       tempArray;

            // 将每行词做拆分加入到总列表容器中
            while ((str = in.readLine()) != null) {
                tempArray = str.split("\t");
                String[] split = tempArray[1].split("_");

                List<Integer> integerList = new ArrayList<>();

                for (String integer : split) {
                    integerList.add(Integer.valueOf(integer));
                }
                words.put(tempArray[0], integerList);
            }

            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }

        return words;
    }

    private Map<String, String> readBsbiDoc() {

        File                file   = new File("index/bsbi.txt");
        Map<String, String> allDoc = new TreeMap<>();
        BufferedReader      br;

        if (!file.exists()) {
            return null;
        }

        try {
            br = new BufferedReader(new FileReader("index/bsbi.txt"));

            String strLine;

            while ((strLine = br.readLine()) != null) {
                String[] split = strLine.split("\t");
                allDoc.put(split[0], split[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allDoc;
    }

    private void writeOutOperation(Document doc1, Map<String, String> allDoc) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("index/bsbi.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (allDoc == null) {
            try {
                Set<String> keySet = doc1._effectWords.keySet();

                Iterator<String> iter = keySet.iterator();

                while (iter.hasNext()) {

                    String        token = iter.next();
                    List<Integer> list  = doc1._effectWords.get(token);

                    String output = token + "\t" + doc1.docId;

                    for (int i = 0; i < list.size(); i++) {
                        output += "_" + list.get(i);
                    }
                    output += "\n";
                    bw.write(output);

                }
                bw.flush();
                bw.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> d1 = new ArrayList<>(doc1._effectWords.keySet());
        List<String> d2 = new ArrayList<>(allDoc.keySet());

        for (int i = 0, j = 0; i < d1.size() && j < d2.size(); ) {
            String output = "";

            if (d1.get(i).compareTo(d2.get(j)) < 0) {
                String token = d1.get(i);
                output += token + "\t" + doc1.docId;
                for (int index : doc1._effectWords.get(token)) {
                    output += "_" + index;
                }
                i++;
            } else if (d1.get(i).compareTo(d2.get(j)) > 0) {
                String token = d2.get(j);
                output = token + "\t" + allDoc.get(token);
                j++;
            } else {
                String token = d1.get(i);
                output = token + "\t" + allDoc.get(token) + ":";
                output += doc1.docId;
                for (int index : doc1._effectWords.get(token)) {
                    output += "_" + index;
                }
                i++;
                j++;
            }

            output += "\n";
            try {
                bw.write(output);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}