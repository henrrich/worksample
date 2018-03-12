package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

/*
 * This class represents the release group information returned in the response body of musicbrainz artist REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseGroup implements Serializable {

    private String id;
    private String title;

    @JsonProperty("primary-type")
    private String primaryType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }
}
