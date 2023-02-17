package com.semapms.sociallogin.api.naver;

import com.semapms.sociallogin.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverProfileRes {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Response {
        private String id;
        private String nickname;
        private String name;
        private String email;
        private String gender;
        private String age;
        private String birthday;
        private String profile_image;
        private String birthyear;
        private String mobile;
    }

    public User toUser() {
        User user = new User();
        user.setEmail(this.response.getEmail());

        return user;
    }
}
