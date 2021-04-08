package it.zysk.spotifyrandomizer.service.auth;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.dto.UserAuthenticationDetails;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientForUserFactory;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientProvider;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // TODO: 08.04.2021 add access token refreshing

    private final SpotifyApiClientProvider spotifyApiClientProvider;
    private final SpotifyApiClientForUserFactory spotifyApiClientForUserFactory;

    private static final List<String> SCOPES = List.of("user-read-private", "user-read-email", "playlist-modify-public");
    private static final String SPACE_DELIMITER = " ";

    public URI buildAuthorizationCodeURI() {
        return spotifyApiClientProvider
                .get()
                .authorizationCodeUri()
                .scope(joinScopesByDelimiter())
                .build()
                .execute();
    }

    public UserAuthenticationDetails exchangeCodeForUserTokens(String authorizationCode) {
        Objects.requireNonNull(authorizationCode); // todo: handle null code
        var authorizationCodeRequest = spotifyApiClientProvider
                .get()
                .authorizationCode(authorizationCode)
                .build();

        try {
            var authorizationCodeCredentials = authorizationCodeRequest.execute();

            // TODO: 03.04.2021 auto mapper
            return UserAuthenticationDetails.builder()
                    .accessToken(authorizationCodeCredentials.getAccessToken())
                    .refreshToken(authorizationCodeCredentials.getRefreshToken())
                    .expiresIn(authorizationCodeCredentials.getExpiresIn())
                    .build();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public SpotifyUser retrieveUserByAccessToken(String accessToken) {
        Objects.requireNonNull(accessToken);

        SpotifyApi spotifyApi = spotifyApiClientForUserFactory.apply(accessToken);

        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                .getCurrentUsersProfile()
                .build();

        try {
            User user = getCurrentUsersProfileRequest.execute();
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

    private static String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, SCOPES);
    }
}
