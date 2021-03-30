package it.zysk.spotifyrandomizer.rest.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@RestController
@Controller
public class SpotifyController {

    private final SpotifyApi spotifyApi;
    private String currentUserDisplayName;

    public SpotifyController(SpotifyProperties spotifyProperties) {
        spotifyApi = SpotifyApi.builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8080/callback"))
                .build();
    }


    @GetMapping("login")
    public String login() {
        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-private user-read-email")
                .build();

        URI execute = authorizationCodeRequest.execute();

        System.out.println("auth code request uri: " + execute.toString());

        return "redirect:" + execute.toString();
    }

    @GetMapping("callback")
    public String callback(@RequestParam("code") String code) throws ParseException, SpotifyWebApiException, IOException {
        System.out.println("auth code response: " + code);

        AuthorizationCodeRequest build = spotifyApi.authorizationCode(code).build();

        AuthorizationCodeCredentials execute = build.execute();

        System.out.println(execute);

        spotifyApi.setAccessToken(execute.getAccessToken());
        spotifyApi.setRefreshToken(execute.getRefreshToken());

        return "redirect:userDetails";
    }

    @GetMapping("userDetails")
    @ResponseBody
    public User userDetails() throws ParseException, SpotifyWebApiException, IOException {
        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                .build();

        User currentUser = getCurrentUsersProfileRequest.execute();

        currentUserDisplayName = currentUser.getDisplayName();

        System.out.println(currentUser);

        return currentUser;
    }

    @GetMapping("playlists")
    @ResponseBody
    public List<PlaylistSimplified> getUserPlaylists() throws ParseException, SpotifyWebApiException, IOException {
        GetListOfCurrentUsersPlaylistsRequest playlistsRequest = spotifyApi.getListOfCurrentUsersPlaylists()
                .build();

        Paging<PlaylistSimplified> execute = playlistsRequest.execute();

        // by default returns only public playlists
        // requires additional scope to access private playlists
        return Optional.ofNullable(execute.getItems())
                .stream()
                .flatMap(Stream::of)
                .filter(playlistSimplified -> playlistSimplified
                        .getOwner()
                        .getDisplayName()
                        .equals(currentUserDisplayName)
                )
                .collect(Collectors.toList());
    }

    @GetMapping("tracks")
    @ResponseBody
    public List<PlaylistTrack> getPlaylistTracks(
            @RequestParam("playlistId") String playlistId
    ) throws ParseException, SpotifyWebApiException, IOException {

        // either I'm using it wrongly or 'fields' in this request do not work as good
        // as described here: https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-playlists-tracks
        GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId)
//                .fields("items(added_at,track(album(images),artists(external_urls,name),id,name,track_number))")
                .build();

        Paging<PlaylistTrack> tracks = getPlaylistsItemsRequest.execute();

        return Optional.ofNullable(tracks.getItems())
                .stream()
                .flatMap(Stream::of)
                .collect(Collectors.toList());
    }
}
