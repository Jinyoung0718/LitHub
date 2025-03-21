package com.sjy.LitHub.record.mapper;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.record.entity.ReadLogStats;

@Component
public class ReadLogStatsMapper {

	public ReadLogStats toEntity(Long userId, int year, int month, int totalReadingTime, int readingCount) {
		return ReadLogStats.builder()
			.user(User.builder().id(userId).build())
			.year(year)
			.month(month)
			.totalReadingTime(totalReadingTime)
			.readingCount(readingCount)
			.averageReadingTime((double) totalReadingTime / readingCount)
			.build();
	}
}