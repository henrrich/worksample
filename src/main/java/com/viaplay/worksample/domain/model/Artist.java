package com.viaplay.worksample.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/*
 * This class represents the artist information returned in the response body of musicbrainz artist REST API
 * It is used as internal data model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist implements Serializable {

    private String id;
    private String name;

    @JsonProperty("relations")
    private List<Relation> relations;

    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;

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

    public List<Relation> getRelations() {
        return relations;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }
}
