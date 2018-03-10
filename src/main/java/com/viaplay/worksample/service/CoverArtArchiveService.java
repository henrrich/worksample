package com.viaplay.worksample.service;

import com.viaplay.worksample.domain.model.AlbumCoverArt;

public interface CoverArtArchiveService {

    AlbumCoverArt getAlbumCoverArt(String mbid);
}
