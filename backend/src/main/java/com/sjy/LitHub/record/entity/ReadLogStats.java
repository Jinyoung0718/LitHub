package com.sjy.LitHub.record.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "read_log_stats", indexes = {
	@Index(name = "idx_readlogstats_user_year_month", columnList = "user_id, year, month"),
	@Index(name = "idx_readlogstats_user_year_total", columnList = "user_id, year, total_reading_time")
})
public class ReadLogStats extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "year", nullable = false)
	private int year;

	@Column(name = "month", nullable = false)
	private int month;

	@Column(name = "total_reading_time", nullable = false)
	private int totalReadingTime;

	@Column(name = "reading_count", nullable = false)
	private int readingCount;

	@Column(name = "average_reading_time", nullable = false)
	@Builder.Default
	private double averageReadingTime = 0.0;
}