package com.inforetrieval.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InversedList {

    private int                   tokenID;
    private String                token;
    private List<Integer>         listDocID;
    private Map<Integer, Integer> mapTokeninDocCount;
    private int                   count;

    public Map<Integer, Integer> getMapTokeninDocCount() {
        return mapTokeninDocCount;
    }
    public void setMapTokeninDocCount(Map<Integer, Integer> mapTokeninDocCount) {
        this.mapTokeninDocCount = mapTokeninDocCount;
    }
    public int getTokenID() {
        return tokenID;
    }
    public void setTokenID(int tokenID) {
        this.tokenID = tokenID;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public List<Integer> getListDocID() {
        return listDocID;
    }
    public void setListDocID(List<Integer> listDocID) {
        this.listDocID = listDocID;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public InversedList() {

        this.setCount(0);
        this.setListDocID(new ArrayList<Integer>());
        this.setMapTokeninDocCount(new TreeMap<Integer, Integer>());

    }

    public void addDocIDtoList(int docID) {
        this.setCount(this.getCount() + 1);
        List<Integer> list = this.getListDocID();
        if (list.contains(docID)) {
            return;
        }
        list.add(docID);
        this.setListDocID(list);
    }

    public void addToMapCount(int docID) {

        if (mapTokeninDocCount.containsKey(docID)) {
            mapTokeninDocCount.put(docID, mapTokeninDocCount.get(docID) + 1);
        }
        else {
            mapTokeninDocCount.put(docID, 1);
        }
    }

}
