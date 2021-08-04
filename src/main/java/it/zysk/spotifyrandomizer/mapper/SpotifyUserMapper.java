package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.specification.User;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpotifyUserMapper {

    SpotifyUser userToSpotifyUser(User user);

    default SpotifyUser userToSpotifyUserWithAccessToken(User user, String accessToken) {
        var spotifyUser = userToSpotifyUser(user);
        spotifyUser.setAccessToken(accessToken);

        return spotifyUser;
    }
}
