package com.sjy.LitHub.record.repository.ReadLogStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.record.entity.QReadLog;
import com.sjy.LitHub.record.entity.QReadLogStats;
import com.sjy.LitHub.record.entity.ReadLogStats;
import com.sjy.LitHub.record.mapper.ReadLogStatsMapper;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.model.ReadingStatsQueryResult;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReadLogStatsRepositoryImpl implements ReadLogStatsRepositoryCustom {

	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;
	private final QReadLogStats readLogStats = QReadLogStats.readLogStats;
	private final QReadLog readLog = QReadLog.readLog;
	private final ReadLogStatsMapper readLogStatsMapper;

	@Override
	public void upsertReadingStats(Long userId, int year, int month, int newReadingTime) {
		long updatedRows = queryFactory
			.update(readLogStats)
			.set(readLogStats.totalReadingTime, readLogStats.totalReadingTime.add(newReadingTime))
			.set(readLogStats.readingCount, readLogStats.readingCount.add(1))
			.set(readLogStats.averageReadingTime,
				readLogStats.totalReadingTime.add(newReadingTime)
					.castToNum(Double.class)
					.divide(readLogStats.readingCount.add(1).castToNum(Double.class))
			)
			.where(readLogStats.user.id.eq(userId)
				.and(readLogStats.year.eq(year))
				.and(readLogStats.month.eq(month)))
			.execute();

		if (updatedRows == 0) {
			ReadLogStats newStats = readLogStatsMapper.toEntity(userId, year, month, newReadingTime, 1);
			entityManager.persist(newStats);
		}
	} // 사용자의 읽기 통계를 업데이트하거나 새로 삽입하는 메소드

	@Override
	public ReadingStatsResponseDTO getReadingStats(Long userId, int year) {
		List<ReadingStatsQueryResult> results = fetchReadingStats(userId, year);
		Set<Integer> mostFrequentMonths = findMostFrequentMonths(userId, year);
		int readingStreak = results.stream()
			.mapToInt(ReadingStatsQueryResult::getReadingStreak)
			.max()
			.orElse(0);

		List<MonthlyReadingStatsResponseDTO> monthlyStats = extractMonthlyStats(results, year, mostFrequentMonths);
		List<ReadingRecordResponseDTO> readingRecords = extractReadingRecords(results);
		return ReadingStatsResponseDTO.of(readingStreak, monthlyStats, readingRecords);
	} // 사용자의 연도별 읽기 통계를 조회하는 메소드

	private List<ReadingStatsQueryResult> fetchReadingStats(Long userId, int year) {
		LocalDate startDate = LocalDate.of(year, 1, 1);
		LocalDate endDate = LocalDate.of(year, 12, 31);

		return queryFactory
			.select(Projections.constructor(ReadingStatsQueryResult.class,
				readLog.streak.max(),
				readLog.date,
				readLog.colorLevel,
				readLogStats.month,
				readLogStats.totalReadingTime,
				readLogStats.readingCount,
				readLogStats.averageReadingTime
			))
			.from(readLog)
			.leftJoin(readLogStats).on(readLog.user.id.eq(readLogStats.user.id)
				.and(readLogStats.year.eq(year)))
			.where(
				readLog.user.id.eq(userId),
				readLog.date.between(startDate, endDate)
			)
			.fetch();
	} // 사용자의 읽기 통계 데이터를 가져오는 메소드

	private Set<Integer> findMostFrequentMonths(Long userId, int year) {
		Integer maxReadingTime = queryFactory
			.select(readLogStats.totalReadingTime.max())
			.from(readLogStats)
			.where(readLogStats.user.id.eq(userId).and(readLogStats.year.eq(year)))
			.fetchOne();

		if (maxReadingTime == null || maxReadingTime == 0) {
			return Collections.emptySet();
		}

		return new HashSet<>(queryFactory
			.select(readLogStats.month)
			.from(readLogStats)
			.where(readLogStats.user.id.eq(userId)
				.and(readLogStats.year.eq(year))
				.and(readLogStats.totalReadingTime.eq(maxReadingTime)))
			.fetch());
	}  // 사용자가 가장 자주 읽은 월을 찾는 메소드

	private List<MonthlyReadingStatsResponseDTO> extractMonthlyStats(List<ReadingStatsQueryResult> results, int year, Set<Integer> mostFrequentMonths) {
		return results.stream()
			.map(result -> MonthlyReadingStatsResponseDTO.from(result, year, mostFrequentMonths))
			.toList();
	}

	private List<ReadingRecordResponseDTO> extractReadingRecords(List<ReadingStatsQueryResult> results) {
		return results.stream()
			.map(ReadingRecordResponseDTO::from)
			.toList();
	}
}