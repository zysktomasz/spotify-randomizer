package it.zysk.spotifyrandomizer.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static it.zysk.spotifyrandomizer.util.SpotifyApiUtil.URIFromString;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
}
