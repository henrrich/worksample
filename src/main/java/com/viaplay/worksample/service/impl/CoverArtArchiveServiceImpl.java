package com.viaplay.worksample.service.impl;

import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.exception.CoverArtNotFoundException;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.service.handler.CoverArtRestErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoverArtArchiveServiceImpl implements CoverArtArchiveService {

    private static final Logger logger = LoggerFactory.getLogger(CoverArtArchiveServiceImpl.class);

    private static final String COVERARTARCHIVE_ALBUM_URL = "http://coverartarchive.org/release-group/";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public AlbumCoverArt getAlbumCoverArt(String mbid) {

        restTemplate.setErrorHandler(new CoverArtRestErrorHandler());

        AlbumCoverArt albumCoverArt = null;
        try {
            albumCoverArt = restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid, AlbumCoverArt.class);
        } catch (RuntimeException e) {
            if (e instanceof CoverArtNotFoundException) {
                logger.warn("Cover art of album with MBID " + mbid + " not found!");
            } else {
                logger.error("Failed to fetch cover art of album with MBID " + mbid);
            }
            albumCoverArt = new AlbumCoverArt();
        }

        return albumCoverArt;
    }
}
