package com.viaplay.worksample.service;

import com.viaplay.worksample.domain.model.Artist;

public interface ArtistService {

    Artist getArtistInfo(String mbid);

    String getProfileDescriptionForArtist(String id);
}
