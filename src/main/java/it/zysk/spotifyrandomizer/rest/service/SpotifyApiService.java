package it.zysk.spotifyrandomizer.rest.service;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
// TODO: 31.03.2021 temp solution show that SpotifyApi could be shared across different components
public class SpotifyApiService {

    private static final List<String> SCOPES = List.of("user-read-private", "user-read-email", "playlist-modify-public");
    private static final String SPACE_DELIMITER = " ";

    @Getter
    private static SpotifyApi spotifyApi;

    public SpotifyApiService(SpotifyProperties spotifyProperties) {
        spotifyApi = SpotifyApi.builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri()))
                .build();
    }

    public static URI buildAuthorizationCodeURI() {
        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi
                .authorizationCodeUri()
                .scope(joinScopesByDelimiter())
                .build();

        return authorizationCodeRequest.execute();
    }

    public static void retrieveAndSetUserCredentials(String authorizationCode) {
        AuthorizationCodeRequest build = getSpotifyApi()
                .authorizationCode(authorizationCode)
                .build();

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = build.execute();
            setCredentials(authorizationCodeCredentials);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }
    }

    public static User getCurrentUsersProfile() {
        Objects.requireNonNull(spotifyApi.getAccessToken());

        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
                .getCurrentUsersProfile()
                .build();

        User user = null;
        try {
            user = getCurrentUsersProfileRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }

        return user;
    }

    public static List<PlaylistSimplified> getCurrentUsersPlaylists() {
        User currentUser = Objects.requireNonNull(getCurrentUsersProfile());

        GetListOfCurrentUsersPlaylistsRequest playlistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .build();

        List<PlaylistSimplified> playlists = List.of();
        try {
            Paging<PlaylistSimplified> paging = playlistsRequest.execute();

            // by default returns only public playlists
            // requires additional scope to access private playlists
            playlists = Optional.ofNullable(paging.getItems())
                    .stream()
                    .flatMap(Stream::of)
                    .filter(playlistSimplified -> playlistSimplified
                            .getOwner()
                            .getDisplayName()
                            .equals(currentUser.getDisplayName())
                    )
                    .collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }

        return playlists;
    }

    public static List<PlaylistTrack> getPlaylistsTracks(String playlistId) {
        Objects.requireNonNull(playlistId);

        // either I'm using it wrongly or 'fields' in this request do not work as good
        // as described here: https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-playlists-tracks
        GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId)
//                .fields("items(added_at,track(album(images),artists(external_urls,name),id,name,track_number))")
                .build();

        List<PlaylistTrack> tracks = List.of();
        try {
            Paging<PlaylistTrack> paging = getPlaylistsItemsRequest.execute();

            tracks = Optional.ofNullable(paging.getItems())
                    .stream()
                    .flatMap(Stream::of)
                    .collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }

        return tracks;
    }

    // TODO: 01.04.2021 it's just proof of concept solution
    public static boolean reorderTracksInPlaylist(String playlistId) {
        Objects.requireNonNull(playlistId);

        // get playlists details
        GetPlaylistRequest getPlaylistRequest = spotifyApi
                .getPlaylist(playlistId)
                .build();

        boolean isSuccessful = false;
        try {
            Playlist playlist = getPlaylistRequest.execute();

            Integer totalTracks = playlist.getTracks().getTotal();

            Random random = new Random();
            for (int i = 0; i < totalTracks; i++) {
                int newTrackPosition = random.nextInt(totalTracks) + 1;

                System.out.println(i + " -> " + newTrackPosition);

                ReorderPlaylistsItemsRequest reorderPlaylistsItemsRequest = spotifyApi
                        .reorderPlaylistsItems(playlistId, i, newTrackPosition)
//                        .snapshot_id(latestSnapshot)
                        .build();

                reorderPlaylistsItemsRequest.execute();
            }
            isSuccessful = true;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }

        return isSuccessful;
    }

    private static void setCredentials(AuthorizationCodeCredentials authorizationCodeCredentials) {
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    }

    private static String joinScopesByDelimiter() {
        return String.join(SPACE_DELIMITER, SCOPES);
    }
}