package it.zysk.spotifyrandomizer.service.authentication.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import it.zysk.spotifyrandomizer.service.authentication.AuthenticationService;
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
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String SPACE_DELIMITER = " ";

    private final SpotifyApiClientFactory spotifyApiClientFactory;
    private final SpotifyApiService spotifyApiService;
    private final JwtService jwtService;
    private final SpotifyProperties spotifyProperties;

    @Override
    public URI buildAuthorizationCodeURI() {
        var authorizationCodeRequest = spotifyApiClientFactory.getSpotifyApi()
                .authorizationCodeUri()
                .scope(this.joinScopesByDelimiter())
                .build();

        return authorizationCodeRequest.execute();
    }

    @Override
    public String authenticateUser(String code) {
        var authorizationCodeCredentials = exchangeCodeUserCredentials(code);
        var spotifyUser = spotifyApiService.getUserByAccessToken(authorizationCodeCredentials.getAccessToken());

        return jwtService.buildSignedJwtForSpotifyUser(spotifyUser);
    }

    private AuthorizationCodeCredentials exchangeCodeUserCredentials(String code) {
        // TODO: 06.07.2021 handle null 'code'
        var authorizationCodeRequest = spotifyApiClientFactory.getSpotifyApi()
                .authorizationCode(code)
                .build();

        try {
            return authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // TODO: 06.07.2021 handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, spotifyProperties.getScopes());
    }
}
