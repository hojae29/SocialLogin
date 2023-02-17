package com.semapms.sociallogin.api.google;

import com.semapms.sociallogin.api.LoginApi;
import com.semapms.sociallogin.api.kakao.KakaoLoginToken;
import com.semapms.sociallogin.api.kakao.KakaoProfileRes;
import com.semapms.sociallogin.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@Component
public class GoogleLogin implements LoginApi {

    private final static String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth";
    private final static String accessTokenUrl = "https://oauth2.googleapis.com/token";
    private final static String profileInfoUrl = "https://people.googleapis.com/v1/people/me?personFields=emailAddresses";

    @Value("${google-redirect-url}")
    private String redirectUri;
    @Value("${google-client-id}")
    private String clientId;
    @Value("${google-client-secret}")
    private String clientSecret;

    @Override
    public boolean supports(String service) {
        return service.equals("google");
    }

    @Override
    public String getAccessToken(Map<String, String> params, String grantType) {
        String uri = UriComponentsBuilder.fromUriString(accessTokenUrl)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", params.get("code"))
                .queryParam("redirect_uri", redirectUri)
                .build().encode().toString();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<GoogleLoginToken> responseType = new ParameterizedTypeReference<GoogleLoginToken>() {
        };

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleLoginToken> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, responseType);

        return response.getBody().getAccess_token();
    }

    @Override
    public User getUserInfo(String token) {
        String uri = UriComponentsBuilder.fromUriString(profileInfoUrl).build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.set(HttpHeaders.ACCEPT, "application/json");


        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<GoogleProfileRes> responseType = new ParameterizedTypeReference<GoogleProfileRes>() {
        };

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleProfileRes> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);

        return response.getBody().toUser();
    }

    @Override
    public String getAuthorizationUrl(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("state", state);

        String authUrl = UriComponentsBuilder.fromUriString(authorizationUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("state", state)
                .build().encode().toUriString();

        return authUrl;
    }
}
