package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserAuthenticationDetails {
    String accessToken;
    String refreshToken; // TODO: 03.04.2021 store refresh token server-side
    Integer expiresIn;
}
