package com.semapms.sociallogin.api.kakao;

import com.semapms.sociallogin.api.LoginApi;
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
public class KakaoLogin implements LoginApi {

    private final static String authorizationUrl = "https://kauth.kakao.com/oauth/authorize";
    private final static String accessTokenUrl = "https://kauth.kakao.com/oauth/token";
    private final static String profileInfoUrl = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao-redirect-url}")
    private String redirectUri;
    @Value("${kakao-client-id}")
    private String clientId;
    @Value("${kakao-client-secret}")
    private String clientSecret;

    @Override
    public boolean supports(String service) {
        return service.equals("kakao");
    }

    @Override
    public String getAccessToken(Map<String, String> params, String grantType) {
        String uri = UriComponentsBuilder.fromUriString(accessTokenUrl)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", params.get("code"))
                .build().encode().toString();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<KakaoLoginToken> responseType = new ParameterizedTypeReference<KakaoLoginToken>() {
        };

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoLoginToken> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);

        return response.getBody().getAccess_token();
    }

    @Override
    public User getUserInfo(String token) {
        String uri = UriComponentsBuilder.fromUriString(profileInfoUrl).build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<KakaoProfileRes> responseType = new ParameterizedTypeReference<KakaoProfileRes>() {};

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoProfileRes> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);

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
                .queryParam("state", state)
                .build().encode().toUriString();

        return authUrl;
    }
}
