package com.BSBI.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Document {
    //文档的唯一标识
    int                        docId;
    //文档的文件地址
    String                     filePath;
    //文档中的有效词
    ArrayList<String>          effectWords;
    //文档中的有效词
    Map<String, List<Integer>> _effectWords;


    public Document(ArrayList<String> effectWords, String filePath) {
        this.effectWords = effectWords;
        this.filePath = filePath;
    }

    public Document(ArrayList<String> effectWords, String filePath, int docId) {
        this(effectWords, filePath);
        this.docId = docId;
    }

    public Document(Map<String, List<Integer>> _effectWords, String filePath, int docId) {
        this._effectWords = _effectWords;
        this.filePath = filePath;
        this.docId = docId;
    }
}