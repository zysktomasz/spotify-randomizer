package it.zysk.spotifyrandomizer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class SpotifyUser {
    String id;
    String displayName;
    String email;
    String accessToken; // TODO: 06.07.2021 do not return access token to front-end, store it server-side
}