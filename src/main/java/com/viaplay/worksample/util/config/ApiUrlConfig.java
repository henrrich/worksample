package com.viaplay.worksample.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.url")
public class ApiUrlConfig {

    @Value("${api.url.musicbrainz}")
    private String apiBaseUrlMusicBrainz;

    @Value("${api.url.coverartarchive}")
    private String apiBaseUrlCoverArtArchive;

    @Value("${api.url.discogs}")
    private String apiBaseUrlDiscogs;

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

}
