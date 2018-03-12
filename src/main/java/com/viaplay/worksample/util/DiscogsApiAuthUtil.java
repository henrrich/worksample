package com.viaplay.worksample.util;

import com.viaplay.worksample.util.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class DiscogsApiAuthUtil {

    @Autowired
    private ApiConfig apiConfig;

    public HttpEntity<String> getAuthHeaderEntity() {
        String authorizationHeader = String.format("Discogs key=%s, secret=%s", apiConfig.getApiDiscogsKey(), apiConfig.getApiDiscogsSecret());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return new HttpEntity<>("parameters", headers);
    }

}
