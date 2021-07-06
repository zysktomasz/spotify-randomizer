package it.zysk.spotifyrandomizer.service.authentication.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import it.zysk.spotifyrandomizer.service.authentication.AuthenticationService;
import it.zysk.spotifyrandomizer.service.jwt.JwtService;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiClientFactory;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final List<String> SCOPES = List.of("user-read-private", "user-read-email");
    private static final String SPACE_DELIMITER = " ";

    private final SpotifyApiClientFactory spotifyApiClientFactory;
    private final SpotifyApiService spotifyApiService;
    private final JwtService jwtService;

    @Override
    public URI buildAuthorizationCodeURI() {
        AuthorizationCodeUriRequest authorizationCodeRequest = this.spotifyApiClientFactory.getSpotifyApi()
                .authorizationCodeUri()
                .scope(joinScopesByDelimiter())
                .build();

        return authorizationCodeRequest.execute();
    }

    @Override
    public String authenticateUser(String code) {
        var authorizationCodeCredentials = this.exchangeCodeUserCredentials(code);
        var spotifyUser = this.spotifyApiService.getUserByAccessToken(authorizationCodeCredentials.getAccessToken());

        return this.jwtService.buildSignedJwtForSpotifyUser(spotifyUser);
    }

    private AuthorizationCodeCredentials exchangeCodeUserCredentials(String code) {
        // TODO: 06.07.2021 handle null 'code'
        var authorizationCodeRequest = this.spotifyApiClientFactory.getSpotifyApi().authorizationCode(code).build();

        try {
            return authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // TODO: 06.07.2021 handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private static String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, SCOPES);
    }
}
