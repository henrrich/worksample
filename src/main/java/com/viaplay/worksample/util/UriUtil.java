package com.viaplay.worksample.util;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class UriUtil {

    public static String getLastUriSegment(String uri) {
        String[] segments =  uri.split("/");
        return segments[segments.length - 1];
    }

    public static String getRequestUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
