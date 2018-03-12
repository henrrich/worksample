package com.viaplay.worksample.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
* A spring bean loads the configuration of external api endpoints defined in application.properties
*/
@Component
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    @Value("${api.url.musicbrainz}")
    private String apiBaseUrlMusicBrainz;

    @Value("${api.url.coverartarchive}")
    private String apiBaseUrlCoverArtArchive;

    @Value("${api.url.discogs}")
    private String apiBaseUrlDiscogs;

    @Value("${api.discogs.key}")
    private String apiDiscogsKey;

    @Value("${api.discogs.secret}")
    private String apiDiscogsSecret;

    public String getApiBaseUrlMusicBrainz() {
        return apiBaseUrlMusicBrainz;
    }

    public void setApiBaseUrlMusicBrainz(String apiBaseUrlMusicBrainz) {
        this.apiBaseUrlMusicBrainz = apiBaseUrlMusicBrainz;
    }

    public String getApiBaseUrlCoverArtArchive() {
        return apiBaseUrlCoverArtArchive;
    }

    public void setApiBaseUrlCoverArtArchive(String apiBaseUrlCoverArtArchive) {
        this.apiBaseUrlCoverArtArchive = apiBaseUrlCoverArtArchive;
    }

    public String getApiBaseUrlDiscogs() {
        return apiBaseUrlDiscogs;
    }

    public void setApiBaseUrlDiscogs(String apiBaseUrlDiscogs) {
        this.apiBaseUrlDiscogs = apiBaseUrlDiscogs;
    }

    public String getApiDiscogsKey() {
        return apiDiscogsKey;
    }

    public void setApiDiscogsKey(String apiDiscogsKey) {
        this.apiDiscogsKey = apiDiscogsKey;
    }

    public String getApiDiscogsSecret() {
        return apiDiscogsSecret;
    }

    public void setApiDiscogsSecret(String apiDiscogsSecret) {
        this.apiDiscogsSecret = apiDiscogsSecret;
    }
}
