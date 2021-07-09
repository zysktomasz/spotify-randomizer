package it.zysk.spotifyrandomizer.util;

import com.wrapper.spotify.SpotifyHttpManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotifyApiUtil {
    public static URI URIFromString(String value) {
        return SpotifyHttpManager.makeUri(value);
    }
}
