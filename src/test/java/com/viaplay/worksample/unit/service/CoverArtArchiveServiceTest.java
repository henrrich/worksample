package com.viaplay.worksample.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.exception.CoverArtNotFoundException;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.service.impl.CoverArtArchiveServiceImpl;
import com.viaplay.worksample.util.ApiUrlConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class CoverArtArchiveServiceTest {

    private static final String COVERARTARCHIVE_ALBUM_BASE_URL = "http://coverartarchive.org/";
    private static final String COVERARTARCHIVE_ALBUM_URL = COVERARTARCHIVE_ALBUM_BASE_URL + "release-group/";

    private static AlbumCoverArt albumCoverArt;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiUrlConfig apiUrlConfig;

    @InjectMocks
    private CoverArtArchiveService coverArtArchiveService = new CoverArtArchiveServiceImpl(new RestTemplateBuilder());

    @BeforeClass
    public static void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = CoverArtArchiveServiceTest.class.getClassLoader();
        albumCoverArt = mapper.readValue(new File(classLoader.getResource("unit/coverart.json").getFile()), AlbumCoverArt.class);
    }

    @Test
    public void testGetAlbumCoverArt() throws ExecutionException, InterruptedException {
        String mbid1 = "e8f70201-8899-3f0c-9e07-5d6495bc8046";
        String mbid2 = "e8f70201-8899-3f0c-9e07-5d6495bc8047";
        String mbid3 = "e8f70201-8899-3f0c-9e07-5d6495bc8048";
        Mockito.when(apiUrlConfig.getApiBaseUrlCoverArtArchive()).thenReturn(COVERARTARCHIVE_ALBUM_BASE_URL);
        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid1, AlbumCoverArt.class)).thenReturn(albumCoverArt);
        assertThat(coverArtArchiveService.getAlbumCoverArt(mbid1).get()).isEqualTo(albumCoverArt);

        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid2, AlbumCoverArt.class)).thenThrow(CoverArtNotFoundException.class);
        CompletableFuture<AlbumCoverArt> actual = coverArtArchiveService.getAlbumCoverArt(mbid2);
        assertThat(actual.get()).isNotNull();
        assertThat(actual.get().getImages()).isEmpty();

        Mockito.when(restTemplate.getForObject(COVERARTARCHIVE_ALBUM_URL + mbid3, AlbumCoverArt.class)).thenThrow(RuntimeException.class);
        actual = coverArtArchiveService.getAlbumCoverArt(mbid3);
        assertThat(actual.get()).isNotNull();
        assertThat(actual.get().getImages()).isEmpty();
    }
}
