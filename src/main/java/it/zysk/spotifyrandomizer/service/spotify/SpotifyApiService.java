package it.zysk.spotifyrandomizer.service.spotify;

import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.model.SpotifyUser;

import java.util.List;

public interface SpotifyApiService {
    SpotifyUser getUserByAccessToken(String accessToken);

    List<PlaylistDTO> getCurrentUsersPlaylists();

    // todo: change return type to DTO with only required fields, once automapper is added
    List<PlaylistTrack> getTracks(String playlistId);
}
