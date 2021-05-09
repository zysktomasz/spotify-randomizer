package it.zysk.spotifyrandomizer.service.auth;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.rest.configuration.SpotifyProperties;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientForUserFactory;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientProvider;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // TODO: 08.04.2021 add access token refreshing

    private final SpotifyApiClientProvider spotifyApiClientProvider;
    private final SpotifyApiClientForUserFactory spotifyApiClientForUserFactory;
    private final JwtService jwtService;
    private final SpotifyProperties spotifyProperties;

    private static final String SPACE_DELIMITER = " ";

    public URI buildAuthorizationCodeURI() {
        return spotifyApiClientProvider
                .get()
                .authorizationCodeUri()
                .scope(joinScopesByDelimiter())
                .build()
                .execute();
    }

    private AuthorizationCodeCredentials exchangeCodeForUserCredentials(String authorizationCode) {
        Objects.requireNonNull(authorizationCode); // todo: handle null code
        var authorizationCodeRequest = spotifyApiClientProvider
                .get()
                .authorizationCode(authorizationCode)
                .build();

        try {
            return authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public String authenticateUser(String authorizationCode) {
        var authorizationCodeCredentials = exchangeCodeForUserCredentials(authorizationCode);
        var spotifyUser = retrieveUserByAccessToken(authorizationCodeCredentials.getAccessToken());

        return jwtService.buildSignedJwt(spotifyUser);
    }

    public SpotifyUser retrieveUserByAccessToken(String accessToken) {
        Objects.requireNonNull(accessToken);

        SpotifyApi spotifyApi = spotifyApiClientForUserFactory.apply(accessToken);

        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                .getCurrentUsersProfile()
                .build();

        try {
            User user = getCurrentUsersProfileRequest.execute();
            // TODO: 24.04.2021 automapper
            return SpotifyUser.builder()
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .email(user.getEmail())
                    .accessToken(accessToken)
                    .build();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, spotifyProperties.getScopes());
    }
}
