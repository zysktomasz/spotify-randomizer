package it.zysk.spotifyrandomizer.rest.controller;

import it.zysk.spotifyrandomizer.rest.configuration.WebappProperties;
import it.zysk.spotifyrandomizer.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import static it.zysk.spotifyrandomizer.util.ControllerConstants.AUTHENTICATION_CONTROLLER_ENDPOINT;

@Controller
@RequestMapping(AUTHENTICATION_CONTROLLER_ENDPOINT)
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String PATH_LOGIN_REQUEST = "login";
    private static final String PATH_CALLBACK_REQUEST = "callback";

    private static final String REQUEST_PARAM_CODE = "code";
    private static final String QUERY_PARAM_JWT = "jwt";

    private final AuthenticationService authenticationService;
    private final WebappProperties webappProperties;

    @GetMapping(PATH_LOGIN_REQUEST)
    public String login() {
        var authenticationCodeURI = authenticationService.buildAuthorizationCodeURI();

        return "redirect:" + authenticationCodeURI.toString();
    }

    @GetMapping(PATH_CALLBACK_REQUEST)
    public String handleAuthenticationCallback(
            @RequestParam(REQUEST_PARAM_CODE) String code
    ) {
        // TODO: 24.04.2021 handle unsuccessful callback response
        String userJwt = authenticationService.authenticateUser(code);
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(webappProperties.getLoginHandlerUrl());
        uriComponentsBuilder.queryParam(QUERY_PARAM_JWT, userJwt);

        return "redirect:" + uriComponentsBuilder.toUriString();
    }

}
