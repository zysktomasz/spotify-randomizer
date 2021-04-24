package it.zysk.spotifyrandomizer.service.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.zysk.spotifyrandomizer.model.SpotifyUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "spotify-randomizer";
    private static final String CLAIM_DISPLAY_NAME = "displayName";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ACCESS_TOKEN = "accessToken";

    private final Key key;

    public JwtService(@Value("${jwt.secretKey}") String jwtSecret) {
        key = buildKeyFromJwtSecret(jwtSecret);
    }

    public String buildSignedJwt(SpotifyUser spotifyUser) {
        return Jwts
                .builder()
                .setSubject(spotifyUser.getEmail())
                .claim(CLAIM_DISPLAY_NAME, spotifyUser.getDisplayName())
                .claim(CLAIM_EMAIL, spotifyUser.getEmail())
                .claim(CLAIM_ACCESS_TOKEN, spotifyUser.getAccessToken()) // TODO: 24.04.2021 do not return access token to front-end, store it server-side
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
//                .setExpiration() // TODO: 24.04.2021 add expiration time limit, once token refreshing is implemented
                .signWith(key)
                .compact();
    }

    // TODO: 24.04.2021 create method that validates JWT

    private Key buildKeyFromJwtSecret(String jwtSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
