package com.sjy.LitHub.account.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NicknameRequestDTO {
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 3, max = 50, message = "닉네임은 3자 이상 50자 이하로 입력해주세요.")
        private String nickName;
}