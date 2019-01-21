package com.DeepIR.domain.po;

public class Result {

    private String url;
    private String title;
    private String content;
    private String time;
    private String website;

    public Result(String url, String title, String content, String time, String website) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.time = time;
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
