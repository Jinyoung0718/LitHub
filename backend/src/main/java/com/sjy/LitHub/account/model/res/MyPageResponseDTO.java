package com.sjy.LitHub.account.model.res;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;

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
public class MyPageResponseDTO {

    @NonNull
    private final UserProfileResponseDTO userProfile;

    @NonNull
    private final ReadingStatsResponseDTO readingStats;

    public static MyPageResponseDTO of(UserProfileResponseDTO userProfile, ReadingStatsResponseDTO readingStats) {
        return MyPageResponseDTO.builder()
            .userProfile(userProfile)
            .readingStats(readingStats)
            .build();
    }
}