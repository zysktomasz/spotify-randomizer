package it.zysk.spotifyrandomizer.rest.filter;

import it.zysk.spotifyrandomizer.model.SpotifyUser;
import it.zysk.spotifyrandomizer.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {

    private static final String BEARER_PREFIX = "Bearer ";

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private FilterChain filterChain;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        httpServletRequest = mock(HttpServletRequest.class);
        httpServletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Not Really a Bearer "})
    void writesUnauthorizedResponseWhenInvalidAuthorizationHeaderIsPresent(String authorizationValue) throws ServletException, IOException {
        // arrange
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authorizationValue);

        // act
        jwtAuthenticationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // assert
        verify(httpServletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void writesUnauthorizedResponseWhenUnableToRetrieveUserFromJWT() throws ServletException, IOException {
        // arrange
        String invalidJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(BEARER_PREFIX + invalidJwt);
        when(jwtService.parseSignedJwt(invalidJwt)).thenReturn(Optional.empty());

        // act
        jwtAuthenticationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // assert
        verify(jwtService, times(1)).parseSignedJwt(invalidJwt);
        verify(httpServletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void storesUserInSecurityContextAndProceedsWithFilterIfValidJWTProvided() throws ServletException, IOException {
        // arrange
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(BEARER_PREFIX + jwt);
        SpotifyUser userFromJwt = SpotifyUser.builder()
                .id("id")
                .displayName("displayName")
                .email("email")
                .accessToken("accessToken")
                .build();
        when(jwtService.parseSignedJwt(jwt)).thenReturn(Optional.of(userFromJwt));

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // act
        jwtAuthenticationFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // assert
        verify(jwtService, times(1)).parseSignedJwt(jwt);

        ArgumentCaptor<Authentication> argumentCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(argumentCaptor.capture());

        Object principal = argumentCaptor.getValue().getPrincipal();
        assertTrue(principal instanceof SpotifyUser);
        SpotifyUser securityContextUser = (SpotifyUser) principal;
        assertEquals(userFromJwt, securityContextUser);
    }
}
