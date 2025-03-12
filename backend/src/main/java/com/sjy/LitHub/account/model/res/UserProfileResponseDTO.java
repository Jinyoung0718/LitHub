package com.sjy.LitHub.account.model.res;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sjy.LitHub.account.entity.authenum.Tier;
import org.springframework.lang.NonNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class UserProfileResponseDTO {

    @NonNull
    private final String email;

    @NonNull
    private final String nickname;

    @NonNull
    private final String profileImageUrlLarge;

    @NonNull
    private final Tier tier;

    @NonNull
    private final Integer point;
}