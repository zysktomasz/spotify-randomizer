package it.zysk.spotifyrandomizer.service.jwt;

import it.zysk.spotifyrandomizer.model.SpotifyUser;

import java.util.Optional;

public interface JwtService {
    String buildSignedJwtForSpotifyUser(SpotifyUser spotifyUser);

    Optional<SpotifyUser> parseSignedJwt(String jwt);
}
