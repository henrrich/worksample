package com.viaplay.worksample.util;

import org.springframework.http.HttpStatus;

/*
* Util class for handling http error codes
*/
public class RestErrorUtil {

    // check if http status code is a client or server error
    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
