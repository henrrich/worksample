package com.viaplay.worksample.util;

import com.viaplay.worksample.util.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/*
* Util class for handling discogs rest api authorization support
*/
@Component
public class DiscogsApiAuthUtil {

    @Autowired
    private ApiConfig apiConfig;

    // method to add discogs api authorization key and secret into http authorization header
    public HttpEntity<String> getAuthHeaderEntity() {
        String authorizationHeader = String.format("Discogs key=%s, secret=%s", apiConfig.getApiDiscogsKey(), apiConfig.getApiDiscogsSecret());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return new HttpEntity<>("parameters", headers);
    }

}
