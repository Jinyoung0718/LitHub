package com.sjy.LitHub.record;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "read_log")
public class ReadLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "reading_time", nullable = false)
    private int readingTime;

    @Setter
    @Column(name = "streak", nullable = false)
    private int streak;
}