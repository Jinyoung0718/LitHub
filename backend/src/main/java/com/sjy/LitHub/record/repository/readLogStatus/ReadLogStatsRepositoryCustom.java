package com.sjy.LitHub.record.repository.readLogStatus;

import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;

public interface ReadLogStatsRepositoryCustom {

	// 읽기 통계 데이터를 업데이트하거나, 해당 데이터가 없으면 새로 삽입하는 메소드
	void upsertReadingStats(Long userId, int year, int month, int newReadingTime);

	// 사용자의 특정 연도에 대한 읽기 통계 데이터를 조회하는 메소드
	ReadingStatsResponseDTO getReadingStats(Long userId, int year);
}