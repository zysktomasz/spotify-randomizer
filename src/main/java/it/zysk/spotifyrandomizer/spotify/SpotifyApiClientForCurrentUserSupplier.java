package it.zysk.spotifyrandomizer.spotify;

import com.wrapper.spotify.SpotifyApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SpotifyApiClientForCurrentUserSupplier implements Supplier<SpotifyApi> {

    private final SpotifyApiClientProvider spotifyApiClientProvider;
    private final SpotifyApiClientForUserFactory spotifyApiClientForUserFactory;

    @Override
    public SpotifyApi get() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SpotifyUser)
                .map(principal -> (SpotifyUser) principal)
                .map(SpotifyUser::getAccessToken)
                .map(spotifyApiClientForUserFactory)
                .orElse(spotifyApiClientProvider.get());
    }
}

