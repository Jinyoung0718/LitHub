package com.sjy.LitHub.record.entity;

import java.time.LocalDate;

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
@Table(name = "read_log", indexes = {
	@Index(name = "idx_readlog_user_date", columnList = "user_id, date") // 특정 날짜에 읽기 여부 확인 및 streak 계산용
})
public class ReadLog extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "reading_time", nullable = false)
	private int readingTime;

	@Column(name = "streak", nullable = false)
	private int streak;

	@Column(name = "color_level", nullable = false)
	private int colorLevel;
}