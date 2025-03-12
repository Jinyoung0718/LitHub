package com.sjy.LitHub.account.mapper;

import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;

public class MyPageMapper {

    public static MyPageResponseDTO toMyPageResponse(UserProfileResponseDTO userProfile, ReadingStatsResponseDTO readingStats) {
        return MyPageResponseDTO.builder()
                .userProfile(userProfile)
                .readingStreak(readingStats.getReadingStreak())
                .monthlyStats(readingStats.getMonthlyStats())
                .readingRecords(readingStats.getReadingRecords())
                .build();
    }
}