package com.semapms.sociallogin.api.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginToken {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
}
