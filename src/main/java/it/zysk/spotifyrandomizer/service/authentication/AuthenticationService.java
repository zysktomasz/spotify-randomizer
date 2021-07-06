package it.zysk.spotifyrandomizer.service.authentication;

import it.zysk.spotifyrandomizer.dto.UserAuthenticationDetailsDTO;

import java.net.URI;

public interface AuthenticationService {
    URI buildAuthorizationCodeURI();

    UserAuthenticationDetailsDTO exchangeCodeForUserTokens(String code);
}
