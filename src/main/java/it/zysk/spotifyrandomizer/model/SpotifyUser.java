package it.zysk.spotifyrandomizer.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpotifyUser {
    String id;
    String displayName;
    String email;
    String accessToken;
}
