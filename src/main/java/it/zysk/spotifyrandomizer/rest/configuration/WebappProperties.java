package it.zysk.spotifyrandomizer.rest.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "webapp")
@EnableConfigurationProperties
@Configuration
@Getter
@Setter
public class WebappProperties {
    String url;
    String loginHandlerUrl;
}
