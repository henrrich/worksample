package com.viaplay.worksample.service;

import com.viaplay.worksample.domain.model.AlbumCoverArt;

import java.util.concurrent.CompletableFuture;

public interface CoverArtArchiveService {

    CompletableFuture<AlbumCoverArt> getAlbumCoverArt(String mbid);
}
