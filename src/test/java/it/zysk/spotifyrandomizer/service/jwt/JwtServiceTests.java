package it.zysk.spotifyrandomizer.service.jwt;

import io.jsonwebtoken.Claims;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import org.junit.jupiter.api.Test;

import static it.zysk.spotifyrandomizer.service.jwt.JwtServiceTestUtils.CLAIM_ACCESS_TOKEN;
import static it.zysk.spotifyrandomizer.service.jwt.JwtServiceTestUtils.CLAIM_DISPLAY_NAME;
import static it.zysk.spotifyrandomizer.service.jwt.JwtServiceTestUtils.CLAIM_EMAIL;
import static it.zysk.spotifyrandomizer.service.jwt.JwtServiceTestUtils.JWT_KEY;
import static it.zysk.spotifyrandomizer.service.jwt.JwtServiceTestUtils.extractClaimsBodyFromStringJwt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceTests {
    private static final SpotifyUser spotifyUser = SpotifyUser.builder()
            .id("id_value")
            .displayName("displayName_value")
            .email("email_value")
            .accessToken("accessToken_value")
            .build();

    private final JwtService jwtService = new JwtService(JWT_KEY);

    @Test
    void Should_HaveExpectedClaims_When_BuiltJwtFromSpotifyUser() {
        int expectedClaimsCount = 6;

        var jwt = jwtService.buildSignedJwtForSpotifyUser(spotifyUser);

        Claims body = extractClaimsBodyFromStringJwt(jwt);

        assertEquals(body.size(), expectedClaimsCount);
        assertEquals(body.getSubject(), spotifyUser.getId());
        assertEquals(body.get(CLAIM_DISPLAY_NAME), spotifyUser.getDisplayName());
        assertEquals(body.get(CLAIM_EMAIL), spotifyUser.getEmail());
        assertEquals(body.get(CLAIM_ACCESS_TOKEN), spotifyUser.getAccessToken());
        assertEquals(body.getIssuer(), JwtServiceTestUtils.ISSUER);
    }
}
