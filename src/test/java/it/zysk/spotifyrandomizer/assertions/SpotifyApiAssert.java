package it.zysk.spotifyrandomizer.assertions;

import com.wrapper.spotify.SpotifyApi;
import org.assertj.core.api.AbstractAssert;

public class SpotifyApiAssert extends AbstractAssert<SpotifyApiAssert, SpotifyApi> {

    private SpotifyApiAssert(SpotifyApi spotifyApi) {
        super(spotifyApi, SpotifyApiAssert.class);
    }

    public static SpotifyApiAssert assertThat(SpotifyApi actual) {
        return new SpotifyApiAssert(actual);
    }

    public void equals(SpotifyApi expected) {
        isNotNull();
        if (!actual.getClientId().equals(expected.getClientId())) {
            failWithActualExpectedAndMessage(
                    actual.getClientId(),
                    expected.getClientId(),
                    "Actual has different clientId"
            );
        }
        if (!actual.getClientSecret().equals(expected.getClientSecret())) {
            failWithActualExpectedAndMessage(actual.getClientSecret(),
                    expected.getClientSecret(),
                    "Actual has different clientSecret"
            );
        }
        if (!actual.getRedirectURI().equals(expected.getRedirectURI())) {
            failWithActualExpectedAndMessage(actual.getRedirectURI(),
                    expected.getRedirectURI(),
                    "Actual has different redirectURI"
            );
        }
        if (propertiesAreNotNull(actual.getAccessToken(), expected.getAccessToken())
                && !actual.getAccessToken().equals(expected.getAccessToken())
        ) {
            failWithActualExpectedAndMessage(actual.getAccessToken(),
                    expected.getAccessToken(),
                    "Actual has different accessToken"
            );
        }
    }

    boolean propertiesAreNotNull(Object actualProperty, Object expectedProperty) {
        return actualProperty != null && expectedProperty != null;
    }
}