package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/*
 * This class represents the relation information returned in the response body of musicbrainz artist REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relation implements Serializable {

    @JsonProperty("url")
    private Url url;

    @JsonProperty("type")
    private String relationType;


    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
}

