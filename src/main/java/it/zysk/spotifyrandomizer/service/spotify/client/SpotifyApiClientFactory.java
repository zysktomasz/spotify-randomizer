package it.zysk.spotifyrandomizer.service.spotify.client;

import com.wrapper.spotify.SpotifyApi;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.rest.configuration.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public SpotifyApi getSpotifyApiForCurrentUser() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SpotifyUser)
                .map(principal -> (SpotifyUser) principal)
                .map(SpotifyUser::getAccessToken)
                .map(this::getSpotifyApiForAccessToken)
                .orElseGet(this::getSpotifyApi);
    }
}
