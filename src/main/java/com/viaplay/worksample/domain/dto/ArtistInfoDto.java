package com.viaplay.worksample.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@ApiModel( description = "Representing information for an artist, including id, profile description and the artist's released albums.")
public class ArtistInfoDto implements Serializable {

    @ApiModelProperty(value = "MBID of the artist")
    private String mbid;

    @ApiModelProperty(value = "artist's profile description", allowEmptyValue = true)
    private String description;

    @ApiModelProperty(value = "A list of artist's albums")
    private List<AlbumDto> albums;

    public ArtistInfoDto() {
    }

    public ArtistInfoDto(String mbid, String description) {
        this.mbid = mbid;
        this.description = description;
        this.albums = new LinkedList<AlbumDto>();
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AlbumDto> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDto> albums) {
        this.albums = albums;
    }

    public void addAlbum(AlbumDto album) {
        this.albums.add(album);
    }

}
