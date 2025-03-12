package com.sjy.LitHub.record.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ReadingRecordResponseDTO {

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NonNull
        private final LocalDate date;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private final int colorLevel;
}