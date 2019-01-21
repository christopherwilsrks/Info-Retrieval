package com.DeepIR.domain.po;

import java.util.List;

public class TotalResults {

    public TotalResults(int total, List<Result> results) {
        this.total = total;
        this.results = results;
    }

    private int          total;
    private List<Result> results;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
