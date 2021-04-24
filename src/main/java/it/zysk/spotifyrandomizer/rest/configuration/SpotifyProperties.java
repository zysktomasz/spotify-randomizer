package it.zysk.spotifyrandomizer.rest.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spotify")
@EnableConfigurationProperties
@Configuration
@Getter
@Setter
public class SpotifyProperties {
    String clientId;
    String clientSecret;
    String redirectUri;
}
