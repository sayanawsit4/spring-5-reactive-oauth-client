package com.baeldung.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class LoginController {


    @Autowired
    WebClient webClient;

    @GetMapping("/auth-code")
    Mono<String> useOauthWithAuthCode(@RegisteredOAuth2AuthorizedClient("vibe") OAuth2AuthorizedClient client) {
        Mono<String> retrievedResource = webClient.get()
                .uri("http://localhost:9000/user")
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class);
        return retrievedResource.map(string -> "We retrieved the following resource using Oauth: " + string);
    }

    @GetMapping("/")
    public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
        return oauth2User
                .map(OAuth2User::getName)
                .map(name -> String.format("Hi, %s", name));
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "WebFlux OAuth example";
    }

}