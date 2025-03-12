package com.sjy.LitHub.account.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponseDTO {
    private Long requestId;
    private String requesterNickName;
    private String requesterProfileImageUrl;
    private LocalDateTime createdAt;
}