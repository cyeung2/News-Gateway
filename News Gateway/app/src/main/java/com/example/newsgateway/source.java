package com.example.newsgateway;

import java.io.Serializable;

public class source implements Serializable {
    private String id = "";
    private String name = "";
    private String category = "";

    public source() {
        this.id = "None";
        this.name = "None";
        this.category = "None";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
