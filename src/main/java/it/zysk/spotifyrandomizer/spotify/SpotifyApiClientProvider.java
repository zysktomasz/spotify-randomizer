package it.zysk.spotifyrandomizer.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SpotifyApiClientProvider implements Supplier<SpotifyApi> {

    private final SpotifyProperties spotifyProperties;

    @Override
    public SpotifyApi get() {
        return SpotifyApi.builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri()))
                .build();
    }
}
