package com.viaplay.worksample.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.controller.ArtistInfoController;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.unit.service.ArtistServiceTest;
import com.viaplay.worksample.util.throttling.RateLimitHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ArtistInfoController.class)
public class ArtistInfoControllerTest {

    private static final String ARTISTINFO_URL = "/api/v1/artistinfo/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private CoverArtArchiveService coverArtArchiveService;

    @MockBean
    private RateLimitHandler rateLimitHandler;

    private static Artist artist;
    private static ArtistProfile profile;
    private static AlbumCoverArt albumCoverArt;

    @BeforeClass
    public static void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = ArtistServiceTest.class.getClassLoader();
        artist = mapper.readValue(new File(classLoader.getResource("unit/artistinfo.json").getFile()), Artist.class);
        profile = mapper.readValue(new File(classLoader.getResource("unit/discogs_profile.json").getFile()), ArtistProfile.class);
        albumCoverArt = mapper.readValue(new File(classLoader.getResource("unit/coverart.json").getFile()), AlbumCoverArt.class);

    }

    @Test
    public void testGetArtistInfo() throws Exception {

        String artistMbid = "65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab";
        String profileId = "15885";
        String albumMbid1 = "09464c93-bd43-4642-ab85-3c600d601390";
        String albumMbid2 = "0da580f2-6768-498f-af9d-2becaddf15e0";
        given(artistService.getArtistInfo(artistMbid)).willReturn(artist);
        given(artistService.getProfileDescriptionForArtist(profileId)).willReturn(CompletableFuture.completedFuture(profile));

        given(coverArtArchiveService.getAlbumCoverArt(albumMbid1)).willReturn(CompletableFuture.completedFuture(albumCoverArt));
        given(coverArtArchiveService.getAlbumCoverArt(albumMbid2)).willReturn(CompletableFuture.completedFuture(new AlbumCoverArt()));

        mockMvc.perform(get(ARTISTINFO_URL + artistMbid))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.mbid", is(artistMbid)))
                .andExpect(jsonPath("$.description", is("American singer")))
                .andExpect(jsonPath("$.albums", hasSize(2)))
                .andExpect(jsonPath("$.albums[*].id", containsInAnyOrder(albumMbid1, albumMbid2)))
                .andExpect(jsonPath("$.albums[*].title", containsInAnyOrder("Black Album", "Ride the Lightning")))
                .andExpect(jsonPath("$.albums[*].images[*]", containsInAnyOrder("http://coverartarchive.org/release/2529f558-970b-33d2-a42c-41ab15a970c6/8202912235.jpg")))
                .andExpect(jsonPath("$.albums[*].images[*]", hasSize(1)));
    }

    @Test
    public void testGetArtistInfoArtistNotFound() throws Exception {

        String artistMbid = "65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab";
        given(artistService.getArtistInfo(artistMbid)).willThrow(ArtistNotFoundException.class);

        mockMvc.perform(get(ARTISTINFO_URL + artistMbid))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.message", is("Artist with MBID " + artistMbid + " not found!")))
                .andExpect(jsonPath("$.path", is(ARTISTINFO_URL + artistMbid)));
    }

    @Test
    public void testGetArtistInfoInternalServerError() throws Exception {

        String artistMbid = "65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab";
        given(artistService.getArtistInfo(artistMbid)).willThrow(new RuntimeException("Failed to access external api."));

        mockMvc.perform(get(ARTISTINFO_URL + artistMbid))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())))
                .andExpect(jsonPath("$.message", is("Failed to access external api.")))
                .andExpect(jsonPath("$.path", is(ARTISTINFO_URL + artistMbid)));
    }

    @Test
    public void testGetArtistInfoRateLimitReached() throws Exception {

        String artistMbid = "65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab";
        String errorMsg = "Reached rate limit " + rateLimitHandler.getRateLimitPerSec() + " request per second!";
        willThrow(new RateLimitingException(errorMsg)).given(rateLimitHandler).checkPermit();

        mockMvc.perform(get(ARTISTINFO_URL + artistMbid))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(header().string("X-RateLimit-Limit", String.valueOf(rateLimitHandler.getRateLimitPerSec())))
                .andExpect(jsonPath("$.status", is(HttpStatus.SERVICE_UNAVAILABLE.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.message", is(errorMsg)))
                .andExpect(jsonPath("$.path", is(ARTISTINFO_URL + artistMbid)));
    }
}
