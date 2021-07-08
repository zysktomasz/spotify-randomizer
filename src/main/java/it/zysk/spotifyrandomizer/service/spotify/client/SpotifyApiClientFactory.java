package it.zysk.spotifyrandomizer.service.spotify.client;

import com.wrapper.spotify.SpotifyApi;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static it.zysk.spotifyrandomizer.util.SpotifyApiUtil.URIFromString;

@Service
@RequiredArgsConstructor
public class SpotifyApiClientFactory {

    private final SpotifyProperties spotifyProperties;

    public SpotifyApi getSpotifyApi() {
        return SpotifyApi.builder()
                .setClientId(this.spotifyProperties.getClientId())
                .setClientSecret(this.spotifyProperties.getClientSecret())
                .setRedirectUri(URIFromString(this.spotifyProperties.getRedirectUri()))
                .build();
    }

    public SpotifyApi getSpotifyApiForAccessToken(String accessToken) {
        var spotifyApi = this.getSpotifyApi();
        spotifyApi.setAccessToken(accessToken);

        return spotifyApi;
    }
}
