package it.zysk.spotifyrandomizer.service.spotify.client;

import com.wrapper.spotify.SpotifyApi;
import it.zysk.spotifyrandomizer.rest.configuration.SpotifyProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SpotifyApiClientTestUtils {

    public static final SpotifyProperties BASIC_SPOTIFY_PROPERTIES = SpotifyProperties.builder()
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("http://localhost:8080/redirectUri")
            .scopes(Arrays.asList("scope1", "scope2"))
            .build();

    public static SpotifyApi buildBasicClient() {
        var spotifyProperties = BASIC_SPOTIFY_PROPERTIES;
        var expectedRedirectURI = prepareRedirectURI(spotifyProperties.getRedirectUri());

        return SpotifyApi.builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(expectedRedirectURI)
                .build();
    }

    public static URI prepareRedirectURI(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            log.error("Unable to prepare URI for uri: " + uriString);

            return null;
        }
    }
}
