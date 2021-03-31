package it.zysk.spotifyrandomizer.rest.controller;

import it.zysk.spotifyrandomizer.rest.service.SpotifyApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@Controller
@RequestMapping("auth")
public class AuthorizationController {

    @GetMapping("login")
    public String login() {
        URI execute = SpotifyApiService.buildAuthorizationCodeURI();

        System.out.println("auth code request uri: " + execute.toString());

        return "redirect:" + execute.toString();
    }

    @GetMapping("callback")
    public String callback(@RequestParam("code") String code) {
        System.out.println("auth code response: " + code);

        SpotifyApiService.retrieveAndSetUserCredentials(code);

        return "redirect:http://localhost:8080/api/userDetails";
    }
}
