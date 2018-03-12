package com.viaplay.worksample.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/*
* This class represents an album with id, title and a list of links to cover art images.
* It is used to construct the JSON response body for the artistinfo REST API
*/
@ApiModel( description = "Representing an album with id, title and a list of links to cover art images")
public class AlbumDto implements Serializable {

    @ApiModelProperty(value = "release group MBID of the album")
    private String id;

    @ApiModelProperty(value = "title of the album")
    private String title;

    @ApiModelProperty(value = "A list of links to albums' cover art images", allowEmptyValue = true)
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
