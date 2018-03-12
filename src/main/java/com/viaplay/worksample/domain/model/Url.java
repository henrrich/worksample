package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/*
 * This class represents the relation url information returned in the response body of musicbrainz artist REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
