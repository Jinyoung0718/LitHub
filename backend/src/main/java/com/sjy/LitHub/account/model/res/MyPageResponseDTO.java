package com.sjy.LitHub.account.model.res;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // Builder  사용 시 기본 생성자 x -> 역직렬화 시 필요하므로 강제 생성
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class") // 역직렬화시 타입 정보를 유지하기 위해
public class MyPageResponseDTO {

    @NonNull // 사용자 기본 정보
    private final UserProfileResponseDTO userProfile;

    @NonNull // 연속 독서 일수
    private final Integer readingStreak;

    @NonNull
    @Builder.Default // 1년치 독서 기록 (깃허브 잔디 스타일)
    private final List<ReadingRecordResponseDTO> readingRecords = Collections.emptyList();

    @NonNull
    @Builder.Default // 월별 독서 통계
    private final List<MonthlyReadingStatsResponseDTO> monthlyStats = Collections.emptyList();
}