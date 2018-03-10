package com.viaplay.worksample.service.impl;

import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.handler.DiscogsRestErrorHandler;
import com.viaplay.worksample.service.handler.MusicBrainzRestErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);

    private static final String MUSICBRAINZ_ARTIST_URL = "http://musicbrainz.org/ws/2/artist/";
    private static final String DISCOGS_ARTIST_URL = "https://api.discogs.com/artists/";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Artist getArtistInfo(String mbid) {
        restTemplate.setErrorHandler(new MusicBrainzRestErrorHandler());
        Artist artist = restTemplate.getForObject(MUSICBRAINZ_ARTIST_URL + mbid + "?&fmt=json&inc=url-rels+release-groups", Artist.class);

        return artist;
    }

    @Override
    public String getProfileDescriptionForArtist(String id) {
        restTemplate.setErrorHandler(new DiscogsRestErrorHandler());

        ArtistProfile artistProfile = null;
        String profile = null;

        try {
            artistProfile = restTemplate.getForObject(DISCOGS_ARTIST_URL + id, ArtistProfile.class);
            profile = artistProfile.getProfile();
        } catch (ArtistNotFoundException e) {
            logger.warn("Artist with id " + id + " not found in Discogs!");
        } catch (RateLimitingException e) {
            logger.warn("Hit rate limit of Discogs service!");
        } catch (RuntimeException e) {
            logger.warn("Failed to access discogs api, error: " + e.getMessage());
        }

        return profile;
    }
}
