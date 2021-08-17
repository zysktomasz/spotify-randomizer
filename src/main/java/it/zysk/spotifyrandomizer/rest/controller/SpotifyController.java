package it.zysk.spotifyrandomizer.rest.controller;

import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackDTO;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static it.zysk.spotifyrandomizer.rest.util.ApiConstants.API_CONTROLLER_ENDPOINT;
import static it.zysk.spotifyrandomizer.rest.util.ApiConstants.FORWARD_SLASH;
import static it.zysk.spotifyrandomizer.rest.util.ApiConstants.SPOTIFY_CONTROLLER_ENDPOINT;

@RestController
@RequestMapping(API_CONTROLLER_ENDPOINT + FORWARD_SLASH + SPOTIFY_CONTROLLER_ENDPOINT)
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyApiService spotifyApiService;
    private static final String PLAYLIST_REQUEST_PATH = "playlist";
    private static final String PLAYLIST_WITH_ID_REQUEST_PATH = PLAYLIST_REQUEST_PATH + "/{playlistId}";
    private static final String PLAYLIST_TRACKS_REQUEST_PATH = PLAYLIST_WITH_ID_REQUEST_PATH + "/tracks";

    @GetMapping(PLAYLIST_REQUEST_PATH)
    public List<PlaylistDTO> getUserPlaylists() {
        return spotifyApiService.getCurrentUsersPlaylists();
    }

    @GetMapping(PLAYLIST_TRACKS_REQUEST_PATH)
    public List<PlaylistTrackDTO> getTracksByPlaylistId(@PathVariable String playlistId) {
        return spotifyApiService.getTracks(playlistId);
    }

    @PutMapping(value = PLAYLIST_WITH_ID_REQUEST_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reorderPlaylistTracks(@PathVariable String playlistId) {
        spotifyApiService.reorderTracksInPlaylist(playlistId);

        return ResponseEntity.ok("success");
    }
}
