package it.zysk.spotifyrandomizer.rest.configuration;

import it.zysk.spotifyrandomizer.rest.filter.AccessTokenAuthenticationFilter;
import it.zysk.spotifyrandomizer.service.auth.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Order(1)
    @Configuration
    public static class ResourcesForAuthenticated extends WebSecurityConfigurerAdapter {

        private final AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;

        public ResourcesForAuthenticated(AuthenticationService authenticationService) {
            this.accessTokenAuthenticationFilter = new AccessTokenAuthenticationFilter(authenticationService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .antMatcher("/api/**")
                    .cors()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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
