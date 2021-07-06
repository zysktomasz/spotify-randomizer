package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserAuthenticationDetailsDTO {
    String accessToken;
    String refreshToken; // TODO: 06.07.2021 store refresh token server-side
}
