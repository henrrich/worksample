package com.viaplay.worksample.util;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.*;

public class RestErrorUtilTest {

    @Test
    public void testIsError() {
        assertThat(RestErrorUtil.isError(HttpStatus.NOT_FOUND)).isTrue();
        assertThat(RestErrorUtil.isError(HttpStatus.SERVICE_UNAVAILABLE)).isTrue();
        assertThat(RestErrorUtil.isError(HttpStatus.OK)).isFalse();
    }
}
