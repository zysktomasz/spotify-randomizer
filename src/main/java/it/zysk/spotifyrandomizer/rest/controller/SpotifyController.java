package it.zysk.spotifyrandomizer.rest.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import it.zysk.spotifyrandomizer.rest.config.SpotifyProperties;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;

//@RestController
@Controller
public class SpotifyController {

    private final SpotifyApi spotifyApi;

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

        User execute = getCurrentUsersProfileRequest.execute();

        System.out.println(execute);

        return execute;
    }
}
