package com.sjy.LitHub.account.model.res;

import java.util.List;

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

    @NonNull
    private final List<StudyGroupHistoryDTO> recentStudyHistories;

    public static MyPageResponseDTO of(
        UserProfileResponseDTO userProfile,
        ReadingStatsResponseDTO readingStats,
        List<StudyGroupHistoryDTO> recentStudyHistories
    ) {
        return MyPageResponseDTO.builder()
            .userProfile(userProfile)
            .readingStats(readingStats)
            .recentStudyHistories(recentStudyHistories)
            .build();
    }
}