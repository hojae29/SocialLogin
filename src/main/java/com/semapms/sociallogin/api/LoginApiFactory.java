package com.semapms.sociallogin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginApiFactory {

    @Autowired
    List<LoginApi> loginApi;

    public LoginApi findLoginApi(String service){
        return loginApi.stream().filter(api -> api.supports(service)).findFirst().get();
    }
}
