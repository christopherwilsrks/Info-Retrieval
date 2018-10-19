package com.inforetrieval.domain;

import java.util.List;

public class DocList {

    private String          token;
    private List<TokenInfo> tokenInfos;

    public List<TokenInfo> getTokenInfos() {
        return tokenInfos;
    }

    public void setTokenInfos(List<TokenInfo> tokenInfos) {
        this.tokenInfos = tokenInfos;
    }

    public DocList(String token, List<TokenInfo> tokenInfos) {
        this.token = token;
        this.tokenInfos = tokenInfos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
