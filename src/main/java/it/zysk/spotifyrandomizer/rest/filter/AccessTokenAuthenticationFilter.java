package it.zysk.spotifyrandomizer.rest.filter;

import it.zysk.spotifyrandomizer.service.auth.AuthenticationService;
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
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) servletRequest;
        var httpServletResponse = (HttpServletResponse) servletResponse;

        var authorization = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            var accessToken = authorization.substring(BEARER_PREFIX.length());
            var spotifyUser = authenticationService.retrieveUserByAccessToken(accessToken);

            if (spotifyUser == null) {
                writeUnauthorizedResponse(httpServletResponse);
            }

            var authentication = new UsernamePasswordAuthenticationToken(spotifyUser, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

        writeUnauthorizedResponse(httpServletResponse);
    }

    public void writeUnauthorizedResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
