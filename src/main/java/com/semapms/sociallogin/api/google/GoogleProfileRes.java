package com.semapms.sociallogin.api.google;

import com.semapms.sociallogin.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleProfileRes {

    private List<EmailAddress> emailAddresses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EmailAddress {
        private String value; //이메일 주소
        private String type; //이메일 주소 유형
        private String formattedType; //형식이 지정된 이메일 주소의 유형
        private String displayName; //이메일 표시 이름
    }

    public User toUser() {
        User user = new User();
        user.setEmail(emailAddresses.get(0).getValue());

        return user;
    }
}
