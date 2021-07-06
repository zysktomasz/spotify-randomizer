package it.zysk.spotifyrandomizer.service.authentication;

import java.net.URI;

public interface AuthenticationService {
    URI buildAuthorizationCodeURI();

    String authenticateUser(String code);
}
