package it.zysk.spotifyrandomizer.rest.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "spotify")
@EnableConfigurationProperties
@Configuration
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyProperties {

    private static final String SPACE_DELIMITER = " ";

    String clientId;
    String clientSecret;
    String redirectUri;
    List<String> scopes;

    public String getScopesAsStringJoinedByComma() {
        return String.join(SPACE_DELIMITER, this.scopes);
    }
}