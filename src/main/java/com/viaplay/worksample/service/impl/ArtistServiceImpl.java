package com.viaplay.worksample.service.impl;

import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.handler.DiscogsRestErrorHandler;
import com.viaplay.worksample.service.handler.MusicBrainzRestErrorHandler;
import com.viaplay.worksample.util.ApiUrlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);

    @Autowired
    private ApiUrlConfig apiUrlConfig;

    private RestTemplate restTemplate;

    public ArtistServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Artist getArtistInfo(String mbid) {
        restTemplate.setErrorHandler(new MusicBrainzRestErrorHandler());
        Artist artist = restTemplate.getForObject(apiUrlConfig.getApiBaseUrlMusicBrainz() + "artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups", Artist.class);

        return artist;
    }

    @Override
    @Async
    public CompletableFuture<ArtistProfile> getProfileDescriptionForArtist(String id) {
        restTemplate.setErrorHandler(new DiscogsRestErrorHandler());

        ArtistProfile artistProfile = null;

        try {
            artistProfile = restTemplate.getForObject(apiUrlConfig.getApiBaseUrlDiscogs() + "artists/" + id, ArtistProfile.class);
        } catch (ArtistNotFoundException e) {
            logger.warn("Artist with id " + id + " not found in Discogs!");
        } catch (RateLimitingException e) {
            logger.warn("Hit rate limit of Discogs service!");
        } catch (RuntimeException e) {
            logger.warn("Failed to access discogs api, error: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(artistProfile);
    }
}
