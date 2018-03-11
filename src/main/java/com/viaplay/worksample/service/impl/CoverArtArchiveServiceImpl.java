package com.viaplay.worksample.service.impl;

import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.exception.CoverArtNotFoundException;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.service.handler.CoverArtRestErrorHandler;
import com.viaplay.worksample.util.config.ApiUrlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class CoverArtArchiveServiceImpl implements CoverArtArchiveService {

    private static final Logger logger = LoggerFactory.getLogger(CoverArtArchiveServiceImpl.class);

    @Autowired
    private ApiUrlConfig apiUrlConfig;

    private RestTemplate restTemplate;

    public CoverArtArchiveServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    @Async
    @Cacheable("coverart")
    public CompletableFuture<AlbumCoverArt> getAlbumCoverArt(String mbid) {

        restTemplate.setErrorHandler(new CoverArtRestErrorHandler());

        AlbumCoverArt albumCoverArt = null;
        try {
            albumCoverArt = restTemplate.getForObject(apiUrlConfig.getApiBaseUrlCoverArtArchive() + "release-group/" + mbid, AlbumCoverArt.class);
        } catch (RuntimeException e) {
            if (e instanceof CoverArtNotFoundException) {
                logger.warn("Cover art of album with MBID " + mbid + " not found!");
            } else {
                logger.error("Failed to fetch cover art of album with MBID " + mbid);
            }
            albumCoverArt = new AlbumCoverArt();
        }

        return CompletableFuture.completedFuture(albumCoverArt);
    }
}
