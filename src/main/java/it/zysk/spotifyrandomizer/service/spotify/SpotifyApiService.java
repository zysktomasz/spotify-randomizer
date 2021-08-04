package it.zysk.spotifyrandomizer.service.spotify;

import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackDTO;
import it.zysk.spotifyrandomizer.model.SpotifyUser;

import java.util.List;

public interface SpotifyApiService {
    SpotifyUser getUserByAccessToken(String accessToken);

    List<PlaylistDTO> getCurrentUsersPlaylists();

    List<PlaylistTrackDTO> getTracks(String playlistId);
}
