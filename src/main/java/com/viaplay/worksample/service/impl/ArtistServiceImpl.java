package com.viaplay.worksample.service.impl;

import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.handler.DiscogsRestErrorHandler;
import com.viaplay.worksample.service.handler.MusicBrainzRestErrorHandler;
import com.viaplay.worksample.util.DiscogsApiAuthUtil;
import com.viaplay.worksample.util.config.ApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/*
* Implementation of artist service
*/
@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private DiscogsApiAuthUtil discogsApiAuthUtil;

    private RestTemplate restTemplate;

    public ArtistServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
    * fetch artist information from musicbrainz artist lookup api
    *
    * @param mbid String mbid of the artist
    * @return Artist artist model
    */
    @Override
    @Cacheable("artist")
    public Artist getArtistInfo(String mbid) {
        restTemplate.setErrorHandler(new MusicBrainzRestErrorHandler());
        Artist artist = restTemplate.getForObject(apiConfig.getApiBaseUrlMusicBrainz() + "artist/" + mbid + "?&fmt=json&inc=url-rels+release-groups", Artist.class);

        return artist;
    }

    /**
     * Fetch artist information from discogs api and handle the error when accessing discogs api
     *
     * The method body is executed in a thread if thread pool is enabled
     *
     * @param id String id of the artist in discogs.com
     * @return CompletableFuture<ArtistProfile> an asynchronous future response containing the artist information, which can be fetched later
     */
    @Override
    @Async
    @Cacheable("profile")
    public CompletableFuture<ArtistProfile> getProfileDescriptionForArtist(String id) {
        restTemplate.setErrorHandler(new DiscogsRestErrorHandler());

        ArtistProfile artistProfile = null;
        ResponseEntity<ArtistProfile> response = null;

        HttpEntity<String> entity = discogsApiAuthUtil.getAuthHeaderEntity();
        try {
            response = restTemplate.exchange(apiConfig.getApiBaseUrlDiscogs() + "artists/" + id, HttpMethod.GET, entity, ArtistProfile.class);
            artistProfile = response.getBody();
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
