package com.sjy.LitHub.account.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

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
}