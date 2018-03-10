package com.viaplay.worksample.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.exception.CoverArtNotFoundException;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.service.impl.CoverArtArchiveServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class CoverArtArchiveServiceTest {

    private static final String COVERARTARCHIVE_ALBUM_URL = "http://coverartarchive.org/release-group/";

    private static AlbumCoverArt albumCoverArt;

    @BeforeClass
    public static void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = CoverArtArchiveServiceTest.class.getClassLoader();
        albumCoverArt = mapper.readValue(new File(classLoader.getResource("unit/coverart.json").getFile()), AlbumCoverArt.class);
    }

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CoverArtArchiveService coverArtArchiveService = new CoverArtArchiveServiceImpl();

    @Test
    public void testGetAlbumCoverArt() {
        String mbid1 = "e8f70201-8899-3f0c-9e07-5d6495bc8046";
        String mbid2 = "e8f70201-8899-3f0c-9e07-5d6495bc8047";
        String mbid3 = "e8f70201-8899-3f0c-9e07-5d6495bc8048";
        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid1, AlbumCoverArt.class)).thenReturn(albumCoverArt);
        assertThat(coverArtArchiveService.getAlbumCoverArt(mbid1)).isEqualTo(albumCoverArt);

        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid2, AlbumCoverArt.class)).thenThrow(CoverArtNotFoundException.class);
        AlbumCoverArt actual = coverArtArchiveService.getAlbumCoverArt(mbid2);
        assertThat(actual).isNotNull();
        assertThat(actual.getImages()).isEmpty();

        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid3, AlbumCoverArt.class)).thenThrow(RuntimeException.class);
        actual = coverArtArchiveService.getAlbumCoverArt(mbid3);
        assertThat(actual).isNotNull();
        assertThat(actual.getImages()).isEmpty();
    }
}
