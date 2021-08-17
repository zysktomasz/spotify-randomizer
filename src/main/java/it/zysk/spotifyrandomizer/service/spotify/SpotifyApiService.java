package it.zysk.spotifyrandomizer.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackDTO;
import it.zysk.spotifyrandomizer.mapper.PlaylistSimplifiedMapper;
import it.zysk.spotifyrandomizer.mapper.PlaylistTrackMapper;
import it.zysk.spotifyrandomizer.mapper.SpotifyUserMapper;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.rest.exception.Validator;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToReorderPlaylistTracks;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrieveCurrentUsersPlaylists;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrieveCurrentUsersProfile;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrievePlaylistTracks;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpotifyApiService {

    private final SpotifyApiClientFactory spotifyApiClientFactory;

    private final PlaylistSimplifiedMapper playlistSimplifiedMapper;
    private final SpotifyUserMapper spotifyUserMapper;
    private final PlaylistTrackMapper playlistTrackMapper;

    public SpotifyUser getUserByAccessToken(String accessToken) {
        Validator.requireNotEmpty(accessToken, "Parameter 'accessToken' is required");
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForAccessToken(accessToken);

        try {
            var user = spotifyApi.getCurrentUsersProfile().build().execute();
            return spotifyUserMapper.userToSpotifyUserWithAccessToken(user, accessToken);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new UnableToRetrieveCurrentUsersProfile(e);
        }
    }

    public List<PlaylistDTO> getCurrentUsersPlaylists() {
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();
        var currentUser = getUserByAccessToken(spotifyApi.getAccessToken());
        Validator.requireNotNull(currentUser, "User is required");

        log.info("Retrieving playlists of user = '{}'", currentUser.getDisplayName());

        int limit = 50;
        int offset = 0;

        List<PlaylistSimplified> playlists = new ArrayList<>();
        try {
            while (true) {
                Paging<PlaylistSimplified> pagedPlaylists = spotifyApi
                        .getListOfCurrentUsersPlaylists()
                        .limit(limit)
                        .offset(offset)
                        .build()
                        .execute();

                PlaylistSimplified[] items = pagedPlaylists.getItems();
                if (items != null) {
                    log.info("Retrieved {}-{} of {} playlists", offset + 1, offset + items.length,
                            pagedPlaylists.getTotal());
                    playlists.addAll(Arrays.asList(items));
                }

                offset += limit;

                if (pagedPlaylists.getNext() == null) {
                    break;
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new UnableToRetrieveCurrentUsersPlaylists(e);
        }

        return playlists.stream()
                .filter(playlist -> playlist.getOwner().getDisplayName().equals(currentUser.getDisplayName()))
                .map(playlistSimplifiedMapper::playlistSimplifiedToPlaylistDTO)
                .toList();
    }

    public List<PlaylistTrackDTO> getTracks(String playlistId) {
        Validator.requireNotEmpty(playlistId, "Parameter 'playlistId' is required");
        log.info("Retrieving tracks for playlist with playlist_id = '{}'", playlistId);
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();

        String fields = "limit,next,offset,previous,total,items(added_at,track(album(id,name,images),artists,id,name,type))";
        int limit = 100;
        int offset = 0;

        List<PlaylistTrack> tracks = new ArrayList<>();
        try {
            while (true) {
                Paging<PlaylistTrack> pagedTracks = spotifyApi.getPlaylistsItems(playlistId)
                        .fields(fields)
                        .limit(limit)
                        .offset(offset)
                        .build()
                        .execute();

                PlaylistTrack[] items = pagedTracks.getItems();
                if (items != null) {
                    log.info("Retrieved {}-{} of {} tracks", offset + 1, offset + items.length,
                            pagedTracks.getTotal());
                    tracks.addAll(Arrays.asList(items));
                }

                offset += limit;

                if (pagedTracks.getNext() == null) {
                    break;
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new UnableToRetrievePlaylistTracks(playlistId, e);
        }

        return tracks.stream()
                .map(playlistTrackMapper::playlistTrackToPlaylistTrackDTO)
                .toList();
    }

    public void reorderTracksInPlaylist(String playlistId) {
        Validator.requireNotEmpty(playlistId, "Parameter 'playlistId' is required");
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();

        try {
            Playlist playlist = spotifyApi
                    .getPlaylist(playlistId)
                    .build()
                    .execute();

            Integer totalTracks = playlist.getTracks().getTotal();

            Random random = new Random();
            for (int i = 0; i < totalTracks; i++) {
                int newTrackPosition = random.nextInt(totalTracks) + 1;

                log.info("{} -> {} (previous -> next track position)", i, newTrackPosition);

                ReorderPlaylistsItemsRequest reorderPlaylistsItemsRequest = spotifyApi
                        .reorderPlaylistsItems(playlistId, i, newTrackPosition)
                        .build();

                reorderPlaylistsItemsRequest.execute();
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new UnableToReorderPlaylistTracks(e);
        }
    }
}
