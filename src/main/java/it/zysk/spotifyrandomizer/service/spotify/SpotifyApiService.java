package it.zysk.spotifyrandomizer.service.spotify;

import it.zysk.spotifyrandomizer.model.SpotifyUser;

public interface SpotifyApiService {
    SpotifyUser getUserByAccessToken(String accessToken);
}
