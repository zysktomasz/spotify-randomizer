package it.zysk.spotifyrandomizer.service.authentication;

import it.zysk.spotifyrandomizer.model.SpotifyUser;

import java.net.URI;

public interface AuthenticationService {
    URI buildAuthorizationCodeURI();

    SpotifyUser authenticateUser(String code);
}
