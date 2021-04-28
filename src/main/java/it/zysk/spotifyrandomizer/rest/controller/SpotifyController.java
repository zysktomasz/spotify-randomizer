package it.zysk.spotifyrandomizer.rest.controller;

import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import it.zysk.spotifyrandomizer.dto.PlaylistSimpleDTO;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static it.zysk.spotifyrandomizer.util.ControllerConstants.API_CONTROLLER_ENDPOINT;
import static it.zysk.spotifyrandomizer.util.ControllerConstants.FORWARD_SLASH;
import static it.zysk.spotifyrandomizer.util.ControllerConstants.SPOTIFY_CONTROLLER_ENDPOINT;

@RestController
@RequestMapping(API_CONTROLLER_ENDPOINT + FORWARD_SLASH + SPOTIFY_CONTROLLER_ENDPOINT)
@RequiredArgsConstructor
public class SpotifyController {

    private static final String USER_DETAILS_REQUEST_PATH = "userDetails";
    private static final String PLAYLISTS_REQUEST_PATH = "playlists";
    private static final String TRACKS_REQUEST_PATH = "tracks";
    private static final String REORDER_REQUEST_PATH = "reorderTracks";

    private static final String PLAYLIST_ID_REQUEST_PRAM = "playlistId";

    private final SpotifyApiService spotifyApiService;

    @GetMapping(USER_DETAILS_REQUEST_PATH)
    public User userDetails() {
        User currentUser = spotifyApiService.getCurrentUsersProfile();

        System.out.println(currentUser);

        return currentUser;
    }

    @GetMapping(PLAYLISTS_REQUEST_PATH)
    public List<PlaylistSimpleDTO> getUserPlaylists() {
        return spotifyApiService.getCurrentUsersPlaylists();
    }

    @GetMapping(TRACKS_REQUEST_PATH)
    public List<PlaylistTrack> getPlaylistTracks(
            @RequestParam(PLAYLIST_ID_REQUEST_PRAM) String playlistId
    ) {
        return spotifyApiService.getPlaylistsTracks(playlistId);
    }

    @PutMapping(REORDER_REQUEST_PATH)
    public boolean reorderTracks(
            @RequestParam(PLAYLIST_ID_REQUEST_PRAM) String playlistId
    ) {
        return spotifyApiService.reorderTracksInPlaylist(playlistId);
    }
}
