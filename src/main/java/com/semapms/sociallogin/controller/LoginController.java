package com.semapms.sociallogin.controller;

import com.semapms.sociallogin.api.LoginApi;
import com.semapms.sociallogin.api.LoginApiFactory;
import com.semapms.sociallogin.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/login/{service}")
public class LoginController {

    @Autowired
    LoginApiFactory loginApiFactory;

    @GetMapping
    public String login(@PathVariable String service, HttpSession session){
        LoginApi api = loginApiFactory.findLoginApi(service);
        String authorizationUrl = api.getAuthorizationUrl(session);

        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/redirect")
    public String callBack(@PathVariable String service, @RequestParam Map<String, String> params, HttpSession session){
        /*
        로그인 인증 성공 -> 토큰 발급 -> 토큰으로 네이버 회원정보 조회 후에 DB 회원 테이블 정보와 비교
        정보가 없으면 회원가입 or 있으면 로그인
         */

        LoginApi api = loginApiFactory.findLoginApi(service);

        // CSRF 공격 방지 세션 토큰 비교
        if (session.getAttribute("state").equals(params.get("state"))) {
            String token = api.getAccessToken(params, "authorization_code");
            User user = api.getUserInfo(token);

            session.setAttribute("user", user);
        }

        return "redirect:/user";
    }
}
