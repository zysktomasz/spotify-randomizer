package it.zysk.spotifyrandomizer.service.spotify.client;

import com.wrapper.spotify.SpotifyApi;
import it.zysk.spotifyrandomizer.assertions.SpotifyApiAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientTestUtils.BASIC_SPOTIFY_PROPERTIES;

@ExtendWith(MockitoExtension.class)
public class SpotifyApiClientFactoryTests {

    private SpotifyApiClientFactory spotifyApiClientFactory;

    @BeforeEach
    void setUp() {
        spotifyApiClientFactory = new SpotifyApiClientFactory(BASIC_SPOTIFY_PROPERTIES);
    }

    @Test
    void Should_BuildBasicSpotifyApiClient_WithSpotifyPropertiesProvided() {
        SpotifyApi expectedSpotifyApi = SpotifyApiClientTestUtils.buildBasicClient();
        SpotifyApi result = spotifyApiClientFactory.getSpotifyApi();

        SpotifyApiAssert.assertThat(result).equals(expectedSpotifyApi);
    }
}
