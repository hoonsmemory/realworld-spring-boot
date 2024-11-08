package io.hoon.realworld.api.controller.user.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonTypeName("user")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class UserSignUpRequest {

    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

    @NotEmpty(message = "이름은 필수입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    private UserSignUpRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public UserSignUpServiceRequest toServiceRequest() {
        return UserSignUpServiceRequest.builder()
                                       .email(email)
                                       .username(username)
                                       .password(password)
                                       .build();
    }
}
