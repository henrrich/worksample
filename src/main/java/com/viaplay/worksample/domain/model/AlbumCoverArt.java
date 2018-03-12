package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/*
 * This class represents the release group information returned in the response body of coverartarchive REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumCoverArt implements Serializable {

    @JsonProperty("images")
    private List<Image> images;

    public AlbumCoverArt() {
        this.images = new LinkedList<Image>();
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}

