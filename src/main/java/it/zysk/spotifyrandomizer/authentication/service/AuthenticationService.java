package it.zysk.spotifyrandomizer.authentication.service;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import it.zysk.spotifyrandomizer.dto.UserAuthenticationDetails;
import it.zysk.spotifyrandomizer.spotify.SpotifyApiClientProvider;
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

    private final SpotifyApiClientProvider spotifyApiClientProvider;

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

    private static String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, SCOPES);
    }
}
