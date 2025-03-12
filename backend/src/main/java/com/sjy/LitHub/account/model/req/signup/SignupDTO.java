package com.sjy.LitHub.account.model.req.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {
        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하로 입력해주세요.")
        private String nickName;

        @Email(message = "잘못된 이메일 형식입니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
        private String userEmail;

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8~15자 이내로 숫자와 소문자를 포함해야 합니다.")
        private String userPassword;
}