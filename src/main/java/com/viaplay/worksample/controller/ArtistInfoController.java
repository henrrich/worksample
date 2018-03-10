package com.viaplay.worksample.controller;

import com.viaplay.worksample.domain.dto.AlbumDto;
import com.viaplay.worksample.domain.dto.ArtistInfoDto;
import com.viaplay.worksample.domain.model.AlbumCoverArt;
import com.viaplay.worksample.domain.model.Artist;
import com.viaplay.worksample.service.ArtistService;
import com.viaplay.worksample.service.CoverArtArchiveService;
import com.viaplay.worksample.unit.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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
            description = artistService.getProfileDescriptionForArtist(artistIdInDiscogs);
        }

        ArtistInfoDto artistInfoDto = new ArtistInfoDto(mbid, description);

        artist.getReleaseGroups().stream()
                .filter( releaseGroup -> "Album".equals(releaseGroup.getPrimaryType()) )
                .forEach( releaseGroup -> {
                    AlbumDto albumDto = new AlbumDto(releaseGroup.getId(), releaseGroup.getTitle());
                    AlbumCoverArt albumCoverArt = coverArtArchiveService.getAlbumCoverArt(releaseGroup.getId());
                    albumCoverArt.getImages().stream().forEach(imageObject -> albumDto.addImage(imageObject.getImage()));
                    artistInfoDto.addAlbum(albumDto);
                } );

        return new ResponseEntity<ArtistInfoDto>(artistInfoDto, HttpStatus.OK);
    }

    private String getArtistIdInDiscogs(Artist artist) throws MalformedURLException {
        List<String> discogsUrls = artist.getRelations().stream().filter(
                relationObject -> "discogs".equals(relationObject.getRelationType())
        ).map(relationObject -> relationObject.getUrl().getResource()).collect(Collectors.toList());

        if (discogsUrls != null && discogsUrls.size() > 0) {
            return UriUtil.getLastUriSegment(new URL(discogsUrls.get(0)).getPath());
        }

        return null;
    }

}
