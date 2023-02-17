package com.semapms.sociallogin.api.naver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginToken {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private Integer expires_in;
    private String error;
    private String error_description;
}
