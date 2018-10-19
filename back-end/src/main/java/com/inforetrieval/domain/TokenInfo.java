package com.inforetrieval.domain;

public class TokenInfo {

    private String docsName;
    private int count;


    public TokenInfo(String docsName, int count) {
        this.docsName = docsName;
        this.count = count;
    }

    public String getDocsName() {
        return docsName;
    }

    public void setDocsName(String docsName) {
        this.docsName = docsName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
