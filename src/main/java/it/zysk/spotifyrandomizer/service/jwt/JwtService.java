package it.zysk.spotifyrandomizer.service.jwt;

import it.zysk.spotifyrandomizer.model.SpotifyUser;

public interface JwtService {
    String buildSignedJwtForSpotifyUser(SpotifyUser spotifyUser);
}
