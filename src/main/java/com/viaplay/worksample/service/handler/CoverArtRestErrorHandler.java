package com.viaplay.worksample.service.handler;

import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.CoverArtNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.util.RestErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class CoverArtRestErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CoverArtRestErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return RestErrorUtil.isError(clientHttpResponse.getStatusCode());
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        String statusText = response.getStatusText();
        logger.error("Response error: {} {}", statusCode, statusText);
        switch (statusCode) {
            case NOT_FOUND:
                throw new ArtistNotFoundException(statusText);
            default:
                throw new RuntimeException("Accessing " + method + " " + url + " error: " + statusText);
        }
    }
}
