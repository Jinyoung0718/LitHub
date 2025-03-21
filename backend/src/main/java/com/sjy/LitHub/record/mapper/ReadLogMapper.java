package com.sjy.LitHub.record.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.record.entity.ReadLog;

@Component
public class ReadLogMapper {

	public ReadLog toEntity(Long userId, LocalDate date, int minutes, int streak) {
		return ReadLog.builder()
			.user(User.builder().id(userId).build())
			.date(date)
			.readingTime(minutes)
			.streak(streak)
			.colorLevel(calculateColorLevel(minutes))
			.build();
	}

	private int calculateColorLevel(int readingTime) {
		if (readingTime <= 10)
			return 1;
		else if (readingTime <= 30)
			return 2;
		else if (readingTime <= 60)
			return 3;
		else if (readingTime <= 120)
			return 4;
		else
			return 5;
	}
}