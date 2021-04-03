package it.zysk.spotifyrandomizer.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SpotifyApiClientProvider implements Supplier<SpotifyApi> {

    private final SpotifyApi spotifyApi;

    public SpotifyApiClientProvider(SpotifyProperties spotifyProperties) {
        spotifyApi = SpotifyApi.builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri()))
                .build();
    }

    @Override
    public SpotifyApi get() {
        return spotifyApi;
    }
}
