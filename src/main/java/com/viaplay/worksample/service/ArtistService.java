package com.viaplay.worksample.service;

import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;

import java.util.concurrent.CompletableFuture;

public interface ArtistService {

    Artist getArtistInfo(String mbid);

    CompletableFuture<ArtistProfile> getProfileDescriptionForArtist(String id);
}
