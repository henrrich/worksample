package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/*
 * This class represents the artist information returned in the response body of discogs REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistProfile implements Serializable {

    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
