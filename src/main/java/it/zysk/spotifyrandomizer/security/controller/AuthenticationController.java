package it.zysk.spotifyrandomizer.security.controller;

import it.zysk.spotifyrandomizer.security.service.AuthenticationService;
import it.zysk.spotifyrandomizer.dto.UserAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;

import static it.zysk.spotifyrandomizer.util.ControllerConstants.AUTHENTICATION_CONTROLLER_ENDPOINT;

@Controller
@RequestMapping(AUTHENTICATION_CONTROLLER_ENDPOINT)
@RequiredArgsConstructor
public class AuthenticationController {

    public static final String LOGIN_REQUEST_PATH = "login";
    public static final String CALLBACK_REQUEST_PATH = "callback";

    public static final String CODE_REQUEST_PARAM = "code";

    private final AuthenticationService authenticationService;

    @GetMapping(LOGIN_REQUEST_PATH)
    public String login() {
        URI authenticationCodeURI = authenticationService.buildAuthorizationCodeURI();

        return "redirect:" + authenticationCodeURI.toString();
    }

    @GetMapping(CALLBACK_REQUEST_PATH)
    @ResponseBody
    public UserAuthenticationDetails callback(
            @RequestParam(CODE_REQUEST_PARAM) String code
    ) {
        return authenticationService.exchangeCodeForUserTokens(code);
    }
}
