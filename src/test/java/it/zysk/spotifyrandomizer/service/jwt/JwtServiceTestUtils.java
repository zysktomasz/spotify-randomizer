package it.zysk.spotifyrandomizer.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtServiceTestUtils {
    public static final String ISSUER = "spotify-randomizer";
    public static final String CLAIM_DISPLAY_NAME = "displayName";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ACCESS_TOKEN = "accessToken";

    public static final String JWT_KEY = "daskldjFddrandomKeyndqm20dDN9D/kFrmY3xdfWZkN0uE=";

    public static Claims extractClaimsBodyFromStringJwt(String jwt) {
        Jws<Claims> claims = Jwts
                .parserBuilder()
                .setSigningKey(JWT_KEY)
                .build()
                .parseClaimsJws(jwt);

        return claims.getBody();
    }
}
