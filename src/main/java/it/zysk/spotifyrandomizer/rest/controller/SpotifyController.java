package it.zysk.spotifyrandomizer.rest.controller;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import it.zysk.spotifyrandomizer.rest.service.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SpotifyController {

    @GetMapping("userDetails")
    public User userDetails() {
        User currentUser = SpotifyApiService.getCurrentUsersProfile();

        System.out.println(currentUser);

        return currentUser;
    }

    @GetMapping("playlists")
    public List<PlaylistSimplified> getUserPlaylists() {
        return SpotifyApiService.getCurrentUsersPlaylists();
    }

    @GetMapping("tracks")
    public List<PlaylistTrack> getPlaylistTracks(
            @RequestParam("playlistId") String playlistId
    ) {
        return SpotifyApiService.getPlaylistsTracks(playlistId);
    }

    @PutMapping("reorderTracks")
    public boolean reorderTracks(
            @RequestParam("playlistId") String playlistId
    ) {
        return SpotifyApiService.reorderTracksInPlaylist(playlistId);
    }
}
