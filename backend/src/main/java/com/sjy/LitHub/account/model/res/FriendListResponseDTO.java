package com.sjy.LitHub.account.model.res;

import com.sjy.LitHub.account.entity.authenum.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponseDTO {
    private Long userId;
    private String nickName;
    private String profileImageUrlSmall;
    private Tier tier;
    private Integer point;
}