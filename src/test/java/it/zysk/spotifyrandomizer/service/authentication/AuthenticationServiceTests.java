package it.zysk.spotifyrandomizer.service.authentication;

import it.zysk.spotifyrandomizer.rest.configuration.SpotifyProperties;
import it.zysk.spotifyrandomizer.service.authentication.impl.AuthenticationServiceImpl;
import it.zysk.spotifyrandomizer.service.jwt.JwtService;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientFactory;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientTestUtils.BASIC_SPOTIFY_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_SCOPE = "scope";

    @Mock
    private SpotifyApiClientFactory spotifyApiClientFactory;

    @Mock
    private SpotifyApiService spotifyApiService;

    @Mock
    private JwtService jwtService;

    private final SpotifyProperties spotifyProperties = BASIC_SPOTIFY_PROPERTIES;


    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        this.authenticationService = new AuthenticationServiceImpl(
                this.spotifyApiClientFactory,
                this.spotifyApiService,
                this.jwtService,
                this.spotifyProperties
        );
    }

    @Test
    void Should_BeValidAuthorizationCodeURI_When_BuiltFromSpotifyProperties() {
        var spotifyApi = SpotifyApiClientTestUtils.buildBasicClient();
        var expectedQuery = this.buildAuthorizationQuery(this.spotifyProperties);
        when(this.spotifyApiClientFactory.getSpotifyApi()).thenReturn(spotifyApi);

        var resultURI = this.authenticationService.buildAuthorizationCodeURI();

        assertEquals(expectedQuery, resultURI.getQuery());
    }

    private String buildAuthorizationQuery(SpotifyProperties spotifyProperties) {
        return PARAM_CLIENT_ID + "=" + spotifyProperties.getClientId() +
                "&" + PARAM_RESPONSE_TYPE + "=code" +
                "&" + PARAM_REDIRECT_URI + "=" + spotifyProperties.getRedirectUri() +
                "&" + PARAM_SCOPE + "=" + String.join(" ", spotifyProperties.getScopes());
    }
}
