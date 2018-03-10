package com.viaplay.worksample.util;

public class UriUtil {

    public static String getLastUriSegment(String uri) {
        String[] segments =  uri.split("/");
        return segments[segments.length - 1];
    }
}
