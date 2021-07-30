package it.zysk.spotifyrandomizer.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientFactory;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    private final SpotifyApiClientFactory spotifyApiClientFactory;

    @Override
    public SpotifyUser getUserByAccessToken(String accessToken) {
        // TODO: 06.07.2021 handle null 'accessToken'

        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForAccessToken(accessToken);

        var getCurrentUsersProfileRequest = spotifyApi
                .getCurrentUsersProfile()
                .build();

        try {
            User user = getCurrentUsersProfileRequest.execute();
            // TODO: 06.07.2021 automapper
            return SpotifyUser.builder()
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .email(user.getEmail())
                    .accessToken(accessToken)
                    .build();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<PlaylistDTO> getCurrentUsersPlaylists() {
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();
        User currentUser = Objects.requireNonNull(getCurrentUsersProfile());

        List<PlaylistDTO> playlists = List.of();
        try {
            Paging<PlaylistSimplified> paging = spotifyApi
                    .getListOfCurrentUsersPlaylists()
                    .build()
                    .execute();

            playlists = Optional.ofNullable(paging.getItems())
                    .stream()
                    .flatMap(Stream::of)
                    .filter(playlist -> this.isPlaylistOwnedByUser(playlist, currentUser))
                    .map(PlaylistDTO::buildFromEntity)
                    .collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return playlists;
    }

    private boolean isPlaylistOwnedByUser(PlaylistSimplified playlist, User user) {
        return playlist.getOwner().getDisplayName().equals(user.getDisplayName());
    }

    private User getCurrentUsersProfile() {
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();
        Objects.requireNonNull(spotifyApi.getAccessToken()); // todo: add custom error handler

        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                .getCurrentUsersProfile()
                .build();

        try {
            return getCurrentUsersProfileRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
