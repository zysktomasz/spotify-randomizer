package it.zysk.spotifyrandomizer.service.authentication;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import it.zysk.spotifyrandomizer.rest.configuration.SpotifyProperties;
import it.zysk.spotifyrandomizer.rest.exception.Validator;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToAuthenticateUser;
import it.zysk.spotifyrandomizer.service.jwt.JwtService;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientFactory;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String SPACE_DELIMITER = " ";

    private final SpotifyApiClientFactory spotifyApiClientFactory;
    private final SpotifyApiService spotifyApiService;
    private final JwtService jwtService;
    private final SpotifyProperties spotifyProperties;

    public URI buildAuthorizationCodeURI() {
        var authorizationCodeRequest = spotifyApiClientFactory.getSpotifyApi()
                .authorizationCodeUri()
                .scope(this.joinScopesByDelimiter())
                .build();

        return authorizationCodeRequest.execute();
    }

    public String authenticateUser(String code) {
        var authorizationCodeCredentials = exchangeCodeUserCredentials(code);
        var spotifyUser = spotifyApiService.getUserByAccessToken(authorizationCodeCredentials.getAccessToken());

        return jwtService.buildSignedJwtForSpotifyUser(spotifyUser);
    }

    private AuthorizationCodeCredentials exchangeCodeUserCredentials(String code) {
        Validator.requireNotEmpty(code, "Parameter 'code' is required");

        try {
            return spotifyApiClientFactory.getSpotifyApi().authorizationCode(code).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new UnableToAuthenticateUser(e);
        }
    }

    private String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, spotifyProperties.getScopes());
    }
}
