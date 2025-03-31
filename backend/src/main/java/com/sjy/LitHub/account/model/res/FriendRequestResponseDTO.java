package com.sjy.LitHub.account.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.account.entity.Friend;
import com.sjy.LitHub.account.entity.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponseDTO {
    @NonNull
    private Long requestId;

    @NonNull
    private String requesterNickName;

    @NonNull
    private String requesterProfileImageUrl;

    @NonNull
    private LocalDateTime createdAt;

    public static FriendRequestResponseDTO of(Friend friend) {
        User requester = friend.getRequester();
        return new FriendRequestResponseDTO(
            friend.getId(),
            requester.getNickName(),
            requester.getProfileImageUrl256(),
            friend.getCreatedAt()
        );
    }
}