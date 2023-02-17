package com.semapms.sociallogin.controller;

import com.semapms.sociallogin.api.naver.NaverLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {

    @Autowired
    NaverLogin naverLogin;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public String userInfo() {
        return "user_info";
    }
}
