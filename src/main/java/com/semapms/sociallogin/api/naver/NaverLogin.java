package com.semapms.sociallogin.api.naver;

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
public class NaverLogin implements LoginApi {

    private final static String authorizationUrl = "https://nid.naver.com/oauth2.0/authorize";
    private final static String accessTokenUrl = "https://nid.naver.com/oauth2.0/token";
    private final static String profileInfoUrl = "https://openapi.naver.com/v1/nid/me";

    @Value("${naver-redirect-url}")
    private String redirectUri;
    @Value("${naver-client-id}")
    private String clientId;
    @Value("${naver-client-secret}")
    private String clientSecret;

    @Override
    public boolean supports(String service) {
        return service.equals("naver");
    }

    /**
     * 토큰 발급, 갱신, 삭제
     * @param params    콜백 파라미터
     * @param grantType 1) 발급:'authorization_code'
     *                  2) 갱신:'refresh_token'
     *                  3) 삭제: 'delete'
     * @return 토큰 반환
     */
    @Override
    public String getAccessToken(Map<String, String> params, String grantType) {
        String uri = UriComponentsBuilder.fromUriString(accessTokenUrl)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", params.get("code"))
                .queryParam("state", params.get("state"))
                .queryParam("refresh_token", params.get("refresh_token")) // Access_token 갱신시 사용
                .build().encode().toString();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<NaverLoginToken> responseType = new ParameterizedTypeReference<NaverLoginToken>() {
        };

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverLoginToken> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);

        return response.getBody().getAccess_token();
    }

    /**
     * 토큰 정보로 회원 정보 조회
     * @param token 인증 후 발급된 접근 토큰
     * @return 회원 정보 반환
     */
    @Override
    public User getUserInfo(String token) {
        String uri = UriComponentsBuilder.fromUriString(profileInfoUrl).build().encode().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<NaverProfileRes> responseType = new ParameterizedTypeReference<NaverProfileRes>() {};

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverProfileRes> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType);

        return response.getBody().toUser();
    }

    /**
     * 인증 페이지(NAVER로그인 페이지)로 가는 URL 생성
     * @param session CSRF공격 방지용 state 값 세션에 등록시 사용
     * @return URL 반환
     */
    @Override
    public String getAuthorizationUrl(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("state", state);

        String authUrl = UriComponentsBuilder.fromUriString(authorizationUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().encode().toUriString();

        return authUrl;
    }
}
