package com.BSBI.util;

import java.io.File;
import java.util.ArrayList;

/**
 * 倒排索引测试类
 *
 * @author lyq
 */
public class Client {
    public static void main(String[] args) {
        //读写缓冲区的大小
        int          readBufferSize;
        int          writeBufferSize;
        String       baseFilePath;
        PreTreatTool preTool;
        //BSBI基于磁盘的外部排序算法
        BSBITool  bTool;
        BSBIIndex bIndex;
        //有效词文件路径
        ArrayList<String> efwFilePaths;
        ArrayList<String> docFilePaths;

        readBufferSize = 10;
        writeBufferSize = 20;
        baseFilePath = "docs/";
        docFilePaths = new ArrayList<>();

        File dir = new File("docs");

        for (File file : dir.listFiles()) {
            docFilePaths.add(file.getPath());
        }

        //文档预处理工具类
        preTool = new PreTreatTool(docFilePaths);
        preTool._preTreatWords();

        //预处理完获取有效词文件路径
        efwFilePaths = preTool.getEFWPaths();
        bIndex = new BSBIIndex(efwFilePaths, readBufferSize, writeBufferSize);
        bIndex.outputInvertedFiles();
//        bTool = new BSBITool(efwFilePaths, readBufferSize, writeBufferSize);
//        bTool.outputInvertedFiles();
    }
}