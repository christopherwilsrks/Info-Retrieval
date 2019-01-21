package com.BSBI.util;

import java.io.*;

public class Compressor {

    private String indexPath;
    private String compressPath;
    private int frontSize;

    public Compressor(int frontSize) {
        this.indexPath = "index/bsbi.txt";
        this.compressPath = "index/compress.txt";
        this.frontSize = frontSize;
    }

    public void process() {

        BufferedReader br;
        BufferedWriter bw;

        try {
            br = new BufferedReader(new FileReader(indexPath));
            bw = new BufferedWriter(new FileWriter(compressPath));

            String strLine;
            String front_token = "[";

            while ((strLine = br.readLine()) != null) {

                // 获取token
                String[] split = strLine.split("\t");
                String token = split[0];
                String output = "";

                // 判断当前token的公共前缀是否符合
                if (token.startsWith(front_token)) {
                    // 符合，那么输出到该公共前缀的后头
                    output = " " + strLine.substring(frontSize, strLine.length() - 1);
                } else {
                    // 不符合就将该token输出，记得先输出'\n'
                    output = "\n";

                    // 对长度大于4的字符串处理
                    if (token.length() >= frontSize) {
                        output += strLine.substring(0, frontSize) + "\t" + strLine.substring(frontSize, strLine.length() - 1);
                        front_token = token.substring(0, frontSize);
                    } else {
                        output += strLine.substring(0, strLine.length() - 1);
                    }
                }
                bw.write(output);
                bw.flush();
            }
            bw.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
