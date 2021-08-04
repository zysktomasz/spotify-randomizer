package it.zysk.spotifyrandomizer.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackDTO;
import it.zysk.spotifyrandomizer.mapper.PlaylistSimplifiedMapper;
import it.zysk.spotifyrandomizer.mapper.PlaylistTrackMapper;
import it.zysk.spotifyrandomizer.mapper.SpotifyUserMapper;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import it.zysk.spotifyrandomizer.service.spotify.client.SpotifyApiClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpotifyApiServiceImpl implements SpotifyApiService {

    private final SpotifyApiClientFactory spotifyApiClientFactory;

    private final PlaylistSimplifiedMapper playlistSimplifiedMapper;
    private final SpotifyUserMapper spotifyUserMapper;
    private final PlaylistTrackMapper playlistTrackMapper;

    @Override
    public SpotifyUser getUserByAccessToken(String accessToken) {
        // TODO: 06.07.2021 handle null 'accessToken'
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForAccessToken(accessToken);

        try {
            var user = spotifyApi.getCurrentUsersProfile().build().execute();
            return spotifyUserMapper.userToSpotifyUserWithAccessToken(user, accessToken);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<PlaylistDTO> getCurrentUsersPlaylists() {
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();
        var currentUser = Objects.requireNonNull(getCurrentUsersProfile());

        List<PlaylistDTO> playlists;
        try {
            Paging<PlaylistSimplified> paging = spotifyApi
                    .getListOfCurrentUsersPlaylists()
                    .build()
                    .execute();

            playlists = Optional.ofNullable(paging.getItems())
                    .stream()
                    .flatMap(Stream::of)
                    .filter(playlist -> this.isPlaylistOwnedByUser(playlist, currentUser))
                    .map(playlistSimplifiedMapper::playlistSimplifiedToPlaylistDTO)
                    .collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return playlists;
    }

    @Override
    public List<PlaylistTrackDTO> getTracks(String playlistId) {
        // todo handle null playlistId
        log.info("Retrieving tracks for playlist with playlist_id = '{}'", playlistId);
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();

        String fields = "limit,next,offset,previous,total,items(added_at,track(album(id,name,images),artists,id,name,type))";
        int limit = 10;
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
                    log.info("Retrieved {}-{} of {} tracks", offset + 1, offset + items.length, pagedTracks.getTotal());
                    tracks.addAll(Arrays.asList(items));
                }

                if (pagedTracks.getNext() == null) {
                    break;
                }

                offset += limit;
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return tracks.stream()
                .map(playlistTrackMapper::playlistTrackToPlaylistTrackDTO)
                .collect(Collectors.toList());
    }

    private boolean isPlaylistOwnedByUser(PlaylistSimplified playlist, User user) {
        return playlist.getOwner().getDisplayName().equals(user.getDisplayName());
    }

    private User getCurrentUsersProfile() {
        var spotifyApi = spotifyApiClientFactory.getSpotifyApiForCurrentUser();
        Objects.requireNonNull(spotifyApi.getAccessToken()); // todo: add custom error handler

        try {
            return spotifyApi
                    .getCurrentUsersProfile()
                    .build()
                    .execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            // todo: handle exception
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
