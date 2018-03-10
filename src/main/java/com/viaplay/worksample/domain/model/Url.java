package com.viaplay.worksample.domain.model;

import java.io.Serializable;

public class Url implements Serializable {

    private String resource;
    private String id;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
