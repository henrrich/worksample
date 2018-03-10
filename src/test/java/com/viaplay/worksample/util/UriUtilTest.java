package com.viaplay.worksample.util;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class UriUtilTest {

    @Test
    public void testGetLastUriSegment() {
        String uri = "api.discogs.com/artists/15885";
        assertThat(UriUtil.getLastUriSegment(uri)).isEqualTo("15885");

    }
}
