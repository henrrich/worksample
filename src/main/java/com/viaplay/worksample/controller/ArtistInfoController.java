package com.viaplay.worksample.controller;

import com.viaplay.worksample.domain.dto.AlbumDto;
import com.viaplay.worksample.domain.dto.ArtistInfoDto;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.domain.model.ArtistProfile;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class ArtistInfoController {

    public static final Logger logger = LoggerFactory.getLogger(ArtistInfoController.class);

    @Autowired
    private ArtistService artistService;

    @Autowired
    private CoverArtArchiveService coverArtArchiveService;

    @RequestMapping(value = "/artistinfo/{mbid}", method = RequestMethod.GET)
    public ResponseEntity<ArtistInfoDto> getArtistInfoByMBID(@PathVariable("mbid") String mbid) throws Exception {
        logger.info("Fetching information of artist with MBID {}", mbid);

        Artist artist = artistService.getArtistInfo(mbid);

        String artistIdInDiscogs = getArtistIdInDiscogs(artist);

        String description = null;
        if (artistIdInDiscogs != null) {
            CompletableFuture<ArtistProfile> profileFuture = artistService.getProfileDescriptionForArtist(artistIdInDiscogs);
            description = profileFuture.get().getProfile();
        }

        ArtistInfoDto artistInfoDto = new ArtistInfoDto(mbid, description);

        Map<String, CompletableFuture<AlbumCoverArt>> futures = new HashMap<>();
        Map<String, AlbumDto> albumsMap = new HashMap<>();
        artist.getReleaseGroups().stream()
                .filter( releaseGroup -> "Album".equals(releaseGroup.getPrimaryType()) )
                .forEach( album -> {
                    String albumId = album.getId();
                    AlbumDto albumDto = new AlbumDto(albumId, album.getTitle());
                    albumsMap.put(albumId, albumDto);
                    CompletableFuture<AlbumCoverArt> albumCoverArtFuture = coverArtArchiveService.getAlbumCoverArt(album.getId());
                    futures.put(albumId, albumCoverArtFuture);
                } );

        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[futures.size()])).join();

        futures.forEach( (albumId, future) -> {
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
