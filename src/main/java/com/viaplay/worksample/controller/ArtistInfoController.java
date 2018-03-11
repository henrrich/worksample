package com.viaplay.worksample.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.viaplay.worksample.domain.dto.AlbumDto;
import com.viaplay.worksample.domain.dto.ArtistInfoDto;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.util.UriUtil;
import com.viaplay.worksample.util.throttling.RateLimitHandler;
import com.viaplay.worksample.util.validator.ValidMBID;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@Validated
@Api(value = "/artistinfo", description = "Operations to fetch artist information", produces = "application/json")
public class ArtistInfoController {

    public static final Logger logger = LoggerFactory.getLogger(ArtistInfoController.class);

    @Autowired
    private ArtistService artistService;

    @Autowired
    private CoverArtArchiveService coverArtArchiveService;

    @Autowired
    private RateLimitHandler rateLimitHandler;

    @GetMapping("/artistinfo/{mbid}")
    @ApiOperation(value = "Get artist information by MBID",
            response = ArtistInfoDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ArtistInfoDto.class, message = "Get artist information successfully including artist description and released albums with cover arts."),
            @ApiResponse(code = 400, message = "Bad Request. Invalid MBID format."),
            @ApiResponse(code = 404, message = "Artist not found."),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "API rate limit reached.")
    })
    public ResponseEntity<ArtistInfoDto> getArtistInfoByMBID(@ApiParam(value = "artist MBID in UUID format\ne.g. 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab", required = true)
                                                             @ValidMBID
                                                             @PathVariable(value = "mbid", required = true)
                                                                     String mbid) throws Exception {
        logger.info("Fetching information of artist with MBID {}", mbid);

        rateLimitHandler.checkPermit();

        Artist artist = artistService.getArtistInfo(mbid);

        String artistIdInDiscogs = getArtistIdInDiscogs(artist);

        CompletableFuture<ArtistProfile> profileFuture = null;
        if (artistIdInDiscogs != null) {
            profileFuture = artistService.getProfileDescriptionForArtist(artistIdInDiscogs);
        }

        Map<String, CompletableFuture<AlbumCoverArt>> futures = new HashMap<>();
        Map<String, AlbumDto> albumsMap = new HashMap<>();
        artist.getReleaseGroups().stream()
                .filter(releaseGroup -> "Album".equals(releaseGroup.getPrimaryType()))
                .forEach(album -> {
                    String albumId = album.getId();
                    AlbumDto albumDto = new AlbumDto(albumId, album.getTitle());
                    albumsMap.put(albumId, albumDto);
                    CompletableFuture<AlbumCoverArt> albumCoverArtFuture = coverArtArchiveService.getAlbumCoverArt(album.getId());
                    futures.put(albumId, albumCoverArtFuture);
                });

        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[futures.size()])).join();

        ArtistProfile profile = profileFuture.get();
        ArtistInfoDto artistInfoDto = new ArtistInfoDto(mbid, profile != null ? profile.getProfile() : null);
        futures.forEach((albumId, future) -> {
            try {
                AlbumDto albumDto = albumsMap.get(albumId);
                future.get().getImages().stream().forEach(image -> albumDto.addImage(image.getImage()));
                artistInfoDto.addAlbum(albumDto);
            } catch (Exception e) {
                logger.error("Failed to fetch cover art for album with MBID " + albumId);
            }
        });

        return new ResponseEntity<ArtistInfoDto>(artistInfoDto, HttpStatus.OK);
    }

    private String getArtistIdInDiscogs(Artist artist) throws MalformedURLException {
        List<String> discogsUrls = artist.getRelations().stream().filter(
                relation -> "discogs".equals(relation.getRelationType())
        ).map(relation -> relation.getUrl().getResource()).collect(Collectors.toList());

        if (discogsUrls != null && discogsUrls.size() > 0) {
            return UriUtil.getLastUriSegment(new URL(discogsUrls.get(0)).getPath());
        }

        return null;
    }

}
