package com.viaplay.worksample.domain.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AlbumDto implements Serializable {

    private String id;
    private String title;
    private List<String> images;

    public AlbumDto() {
    }

    public AlbumDto(String id, String title) {
        this.id = id;
        this.title = title;
        this.images = new LinkedList<>();
    }

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void addImage(String image) {
        this.images.add(image);
    }

}
