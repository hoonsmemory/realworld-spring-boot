package io.hoon.realworld.api.controller.user.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonRootName("user")
public class UserLoginRequest {

    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    private UserLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginServiceRequest toServiceRequest() {
        return UserLoginServiceRequest.builder()
                                      .email(email)
                                      .password(password)
                                      .build();
    }
}
