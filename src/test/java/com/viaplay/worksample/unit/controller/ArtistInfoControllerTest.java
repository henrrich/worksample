package com.viaplay.worksample.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.controller.ArtistInfoController;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.unit.service.ArtistServiceTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ArtistInfoController.class)
public class ArtistInfoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private CoverArtArchiveService coverArtArchiveService;

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

        mockMvc.perform(get("/api/v1/artistinfo/" + artistMbid))
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

        mockMvc.perform(get("/api/v1/artistinfo/" + artistMbid))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is("Artist with MBID " + artistMbid + " not found!")));
    }
}
