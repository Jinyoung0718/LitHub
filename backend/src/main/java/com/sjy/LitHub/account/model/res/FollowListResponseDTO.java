package com.sjy.LitHub.account.model.res;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.file.util.FileConstant;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowListResponseDTO {

    private Long userId;

    private String nickName;

    private String profileImageUrlSmall;

    private Tier tier;

    private int point;

    public FollowListResponseDTO(Long userId, String nickName, String storageKey, Tier tier, int point) {
        this.userId = userId;
        this.nickName = nickName;
        this.profileImageUrlSmall = storageKey != null ? FileConstant.publicUrl(storageKey) : null;
        this.tier = tier;
        this.point = point;
    }

    public static FollowListResponseDTO of(User user) {
        return new FollowListResponseDTO(
            user.getId(),
            user.getDisplayNickname(),
            user.getProfileImageUrl256(),
            user.getTier(),
            user.getPoint()
        );
    }
}