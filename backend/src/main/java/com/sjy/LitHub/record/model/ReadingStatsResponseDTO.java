package com.sjy.LitHub.record.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ReadingStatsResponseDTO {

    @NonNull
    private final Integer readingStreak;

    @NonNull
    @Builder.Default
    private final List<ReadingRecordResponseDTO> readingRecords = Collections.emptyList();

    @NonNull
    @Builder.Default
    private final List<MonthlyReadingStatsResponseDTO> monthlyStats = Collections.emptyList();
}