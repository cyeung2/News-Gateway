package com.example.newsgateway;

import java.io.Serializable;

public class story implements Serializable {
    private String author = "";
    private String title = "";
    private String desc = "";
    private String url = "";
    private String urlImg = "";
    private String pubAt = "";
    private String name = "";

    public story() {
        this.author = "None";
        this.title = "None";
        this.desc = "None";
        this.url = "None";
        this.urlImg = "None";
        this.pubAt = "None";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getPubAt() {
        return pubAt;
    }

    public void setPubAt(String pubAt) {
        this.pubAt = pubAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
