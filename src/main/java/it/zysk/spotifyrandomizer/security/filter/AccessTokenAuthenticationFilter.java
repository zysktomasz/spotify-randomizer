package it.zysk.spotifyrandomizer.security.filter;

import it.zysk.spotifyrandomizer.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter implements Filter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) servletRequest;
        var httpServletResponse = (HttpServletResponse) servletResponse;

        var authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            var accessToken = authorization.substring(BEARER_PREFIX.length());
            var spotifyUser = authenticationService.retrieveUserByAccessToken(accessToken);

            if (spotifyUser == null) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }

            var authentication = new UsernamePasswordAuthenticationToken(spotifyUser, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
