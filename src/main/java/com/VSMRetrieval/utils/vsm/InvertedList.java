package com.VSMRetrieval.utils.vsm;

import java.io.Serializable;
import java.util.TreeMap;

public class InvertedList implements Serializable {

    //词项记录表，Token:Docs:Counts
    private TreeMap<String, Integer> mapTF;
    //文档频率，Document Frequency
    private int DF;

    public TreeMap<String, Integer> getMapTF() {
        return mapTF;
    }
    public void setMapTF(TreeMap<String, Integer> mapTF) {
        this.mapTF = mapTF;
    }
    public int getDF() {
        return DF;
    }
    public void setDF(int DF) {
        this.DF = DF;
    }

    public InvertedList() {
        this.setMapTF(new TreeMap<>());
        this.setDF(0);
    }

}
