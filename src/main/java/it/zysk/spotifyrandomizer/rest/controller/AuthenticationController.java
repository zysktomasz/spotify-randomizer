package it.zysk.spotifyrandomizer.rest.controller;

import it.zysk.spotifyrandomizer.rest.configuration.WebAppProperties;
import it.zysk.spotifyrandomizer.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static it.zysk.spotifyrandomizer.rest.util.ApiConstants.AUTHENTICATION_CONTROLLER_ENDPOINT;

@Controller
@RequestMapping(AUTHENTICATION_CONTROLLER_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final WebAppProperties webAppProperties;

    private static final String LOGIN_ENDPOINT = "login";
    private static final String CALLBACK_ENDPOINT = "callback";

    private static final String CODE_REQUEST_PARAM = "code";
    private static final String ERROR_REQUEST_PARAM = "error";

    private static final String QUERY_PARAM_JWT = "jwt";

    @GetMapping(LOGIN_ENDPOINT)
    public String login() {
        URI authenticationURI = this.authenticationService.buildAuthorizationCodeURI();

        return "redirect:" + authenticationURI.toString();
    }

    @GetMapping(CALLBACK_ENDPOINT)
    public String callback(
            @RequestParam(value = CODE_REQUEST_PARAM, required = false) String code,
            @RequestParam(value = ERROR_REQUEST_PARAM, required = false) String error) {
        if (error != null && !error.isBlank()) {
            log.info("Unable to authenticate user. Error: {}", error);
            // TODO: 06.07.2021 handle authentication error (user might've denied access or another reason)
        }

        var userJwt = this.authenticationService.authenticateUser(code);
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(webAppProperties.getSuccessfulLoginHandlerUrl());
        uriComponentsBuilder.queryParam(QUERY_PARAM_JWT, userJwt);

        return "redirect:" + uriComponentsBuilder.toUriString();
    }
}
