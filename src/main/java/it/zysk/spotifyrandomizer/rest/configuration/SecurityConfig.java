package it.zysk.spotifyrandomizer.rest.configuration;

import it.zysk.spotifyrandomizer.rest.filter.JwtAuthenticationFilter;
import it.zysk.spotifyrandomizer.service.jwt.JwtService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final WebAppProperties webAppProperties;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        Long MAX_AGE = 3600L;
        List<String> allowedHeaders = List.of(CONTENT_TYPE, AUTHORIZATION);
        List<String> allowedMethods = Stream.of(POST, GET, PUT, DELETE, OPTIONS)
                .map(HttpMethod::name)
                .toList();

        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(webAppProperties.getHost()));
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setMaxAge(MAX_AGE);
        configuration.setAllowCredentials(true);

        var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return urlBasedCorsConfigurationSource;
    }


    @Order(1)
    @Configuration
    public static class ResourcesForAuthenticated extends WebSecurityConfigurerAdapter {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public ResourcesForAuthenticated(JwtService jwtService) {
            this.jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .antMatcher("/api/**")
                    .csrf().disable()
                    .cors()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Order(2)
    @Configuration
    @AllArgsConstructor
    public static class ResourcesPermittedForAll extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

}