package com.semapms.sociallogin.api.kakao;

import com.semapms.sociallogin.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoProfileRes {
    private Long id;
    private String connected_at;
    private KakaoAccount kakao_account;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class KakaoAccount {
        private String email;
    }

    public User toUser(){
        User user = new User();
        user.setEmail(this.kakao_account.getEmail());

        return user;
    }
}
