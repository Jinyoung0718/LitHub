package com.sjy.LitHub.account.model.req.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialSignupDTO {
        @JsonProperty("nickName")
        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하로 입력해주세요.")
        private String nickName;

        @JsonProperty("userPassword")
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        private String userPassword;
}
