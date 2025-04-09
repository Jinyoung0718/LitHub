package com.sjy.LitHub.account.model.res;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponseDTO {
    @NonNull
    private Long userId;

    @NonNull
    private String nickName;

    @NonNull
    private String profileImageUrlSmall;

    @NonNull
    private Tier tier;

    @NonNull
    private int point;

    public static FriendListResponseDTO of(User user) {
        return new FriendListResponseDTO(
            user.getId(),
            user.getDisplayNickname(),
            user.getProfileImageUrl256(),
            user.getTier(),
            user.getPoint()
        );
    }
}