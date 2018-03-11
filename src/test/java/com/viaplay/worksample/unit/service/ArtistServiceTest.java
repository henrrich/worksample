package com.viaplay.worksample.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.impl.ArtistServiceImpl;
import com.viaplay.worksample.util.config.ApiUrlConfig;
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
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceTest {

    private static final String MUSICBRAINZ_ARTIST_BASE_URL = "http://musicbrainz.org/ws/2/";
    private static final String MUSICBRAINZ_ARTIST_URL = MUSICBRAINZ_ARTIST_BASE_URL + "artist/65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab?&fmt=json&inc=url-rels+release-groups";
    private static final String DISCOGS_ARTIST_BASE_URL = "https://api.discogs.com/";
    private static final String DISCOGS_ARTIST_URL = DISCOGS_ARTIST_BASE_URL + "artists/";

    private static Artist artist;
    private static ArtistProfile profile;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiUrlConfig apiUrlConfig;

    @InjectMocks
    private ArtistService artistService = new ArtistServiceImpl(new RestTemplateBuilder());

    @BeforeClass
    public static void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = ArtistServiceTest.class.getClassLoader();
        artist = mapper.readValue(new File(classLoader.getResource("unit/artistinfo.json").getFile()), Artist.class);
        profile = mapper.readValue(new File(classLoader.getResource("unit/discogs_profile.json").getFile()), ArtistProfile.class);
    }

    @Test
    public void testGetArtistInfo() {
        Mockito.when(restTemplate.getForObject(MUSICBRAINZ_ARTIST_URL, Artist.class)).thenReturn(artist);
        Mockito.when(apiUrlConfig.getApiBaseUrlMusicBrainz()).thenReturn(MUSICBRAINZ_ARTIST_BASE_URL);
        assertThat(artistService.getArtistInfo("65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab")).isEqualTo(artist);
    }

    @Test
    public void testGetProfileDescriptionForArtist() throws ExecutionException, InterruptedException {
        Mockito.when(apiUrlConfig.getApiBaseUrlDiscogs()).thenReturn(DISCOGS_ARTIST_BASE_URL);
        Mockito.when(restTemplate.getForObject(DISCOGS_ARTIST_URL + "15885", ArtistProfile.class)).thenReturn(profile);
        assertThat(artistService.getProfileDescriptionForArtist("15885").get().getProfile()).isEqualTo("American singer");
    }

    @Test
    public void testGetProfileDescriptionFailed() throws ExecutionException, InterruptedException {
        Mockito.when(apiUrlConfig.getApiBaseUrlDiscogs()).thenReturn(DISCOGS_ARTIST_BASE_URL);
        Mockito.when(restTemplate.getForObject(DISCOGS_ARTIST_URL + "15885", ArtistProfile.class)).thenThrow(RuntimeException.class);
        assertThat(artistService.getProfileDescriptionForArtist("15885").get()).isNull();

        Mockito.when(restTemplate.getForObject(DISCOGS_ARTIST_URL + "15886", ArtistProfile.class)).thenThrow(ArtistNotFoundException.class);
        assertThat(artistService.getProfileDescriptionForArtist("15886").get()).isNull();

        Mockito.when(restTemplate.getForObject(DISCOGS_ARTIST_URL + "15887", ArtistProfile.class)).thenThrow(RateLimitingException.class);
        assertThat(artistService.getProfileDescriptionForArtist("15887").get()).isNull();
    }
}
