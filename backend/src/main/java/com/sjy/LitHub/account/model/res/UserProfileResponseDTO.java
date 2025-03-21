package com.sjy.LitHub.account.model.res;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.account.entity.authenum.Tier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class UserProfileResponseDTO {

    @NonNull
    private final Long userId;

    @NonNull
    private final String email;

    @NonNull
    private final String nickname;

    @NonNull
    private final String profileImageUrlLarge;

    @NonNull
    private final Tier tier;

    @NonNull
    private final int point;
}