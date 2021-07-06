package it.zysk.spotifyrandomizer.service.authentication.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import it.zysk.spotifyrandomizer.dto.UserAuthenticationDetailsDTO;
import it.zysk.spotifyrandomizer.service.authentication.AuthenticationService;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiClientFactory;
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

    @Override
    public URI buildAuthorizationCodeURI() {
        AuthorizationCodeUriRequest authorizationCodeRequest = this.spotifyApiClientFactory.getSpotifyApi()
                .authorizationCodeUri()
                .scope(joinScopesByDelimiter())
                .build();

        return authorizationCodeRequest.execute();
    }

    @Override
    public UserAuthenticationDetailsDTO exchangeCodeForUserTokens(String code) {
        // TODO: 06.07.2021 handle null 'code'
        var authorizationCodeRequest = this.spotifyApiClientFactory.getSpotifyApi().authorizationCode(code).build();

        try {
            var authorizationCodeCredentials = authorizationCodeRequest.execute();

            // TODO: 06.07.2021 auto mapper
            return UserAuthenticationDetailsDTO.builder()
                    .accessToken(authorizationCodeCredentials.getAccessToken())
                    .refreshToken(authorizationCodeCredentials.getRefreshToken())
                    .build();
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
