package it.zysk.spotifyrandomizer.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.dto.PlaylistSimpleDTO;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientForCurrentUserSupplier;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SpotifyApiService {

    private final SpotifyApiClientForCurrentUserSupplier spotifyApiClientForCurrentUserSupplier;

    public User getCurrentUsersProfile() {
        var spotifyApi = spotifyApiClientForCurrentUserSupplier.get();
        Objects.requireNonNull(spotifyApi.getAccessToken());

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

    public List<PlaylistSimpleDTO> getCurrentUsersPlaylists() {
        var spotifyApi = spotifyApiClientForCurrentUserSupplier.get();
        User currentUser = Objects.requireNonNull(getCurrentUsersProfile());

        GetListOfCurrentUsersPlaylistsRequest playlistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .build();

        List<PlaylistSimpleDTO> playlists = List.of();
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
                    .map(PlaylistSimpleDTO::buildFromEntity)
                    .collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
        }

        return playlists;
    }

    public List<PlaylistTrack> getPlaylistsTracks(String playlistId) {
        var spotifyApi = spotifyApiClientForCurrentUserSupplier.get();
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
    public boolean reorderTracksInPlaylist(String playlistId) {
        var spotifyApi = spotifyApiClientForCurrentUserSupplier.get();
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
}