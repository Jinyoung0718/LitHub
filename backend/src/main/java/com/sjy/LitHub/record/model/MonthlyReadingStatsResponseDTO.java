package com.sjy.LitHub.record.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class") // 역직렬화 시 타입 정보 유지
public class MonthlyReadingStatsResponseDTO {

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final int year;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final int month;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final int totalReadingTime;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final double averageReadingTime;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final boolean isMostFrequent;
}