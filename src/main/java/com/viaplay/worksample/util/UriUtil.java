package com.viaplay.worksample.util;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/*
* Util class for handling URI string
*/
public class UriUtil {

    // return the last segment of an URI, e.g. /api.discogs.com/artists/18839 will return 18839
    public static String getLastUriSegment(String uri) {
        String[] segments =  uri.split("/");
        return segments[segments.length - 1];
    }

    // fetch request uri from WebRequest object
    public static String getRequestUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
