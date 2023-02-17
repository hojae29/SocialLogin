package com.semapms.sociallogin.api;

import com.semapms.sociallogin.model.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface LoginApi {
    boolean supports(String service);

    String getAuthorizationUrl(HttpSession session);

    String getAccessToken(Map<String, String> params, String grantType);

    User getUserInfo(String token);
}