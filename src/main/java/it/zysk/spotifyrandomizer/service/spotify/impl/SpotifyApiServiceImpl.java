package it.zysk.spotifyrandomizer.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiClientFactory;
import it.zysk.spotifyrandomizer.service.spotify.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    private final SpotifyApiClientFactory spotifyApiClientFactory;

    @Override
    public SpotifyUser getUserByAccessToken(String accessToken) {
        // TODO: 06.07.2021 handle null 'accessToken'

        var spotifyApi = this.spotifyApiClientFactory.getSpotifyApiForAccessToken(accessToken);

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
}
