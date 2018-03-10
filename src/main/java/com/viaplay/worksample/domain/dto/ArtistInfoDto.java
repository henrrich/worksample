package com.viaplay.worksample.domain.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ArtistInfoDto implements Serializable {

    private String mbid;
    private String description;
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
