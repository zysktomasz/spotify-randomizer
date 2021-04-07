package it.zysk.spotifyrandomizer.spotify;

import com.wrapper.spotify.SpotifyApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SpotifyApiClientForUserFactory implements Function<String, SpotifyApi> {

    private final SpotifyApiClientProvider spotifyApiClientProvider;

    @Override
    public SpotifyApi apply(String accessToken) {
        SpotifyApi spotifyApi = spotifyApiClientProvider.get();
        spotifyApi.setAccessToken(accessToken);

        return spotifyApi;
    }
}
