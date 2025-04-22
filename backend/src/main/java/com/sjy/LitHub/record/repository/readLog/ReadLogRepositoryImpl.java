package com.sjy.LitHub.record.repository.readLog;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.record.entity.QReadLog;
import com.sjy.LitHub.record.entity.ReadLog;
import com.sjy.LitHub.record.mapper.ReadLogMapper;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReadLogRepositoryImpl implements ReadLogRepositoryCustom {

	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;
	private final QReadLog readLog = QReadLog.readLog;
	private final ReadLogMapper readLogMapper;

	@Override
	public void saveOrUpdateReadLog(Long userId, LocalDate date, int minutes) {
		ReadLog existingLog = queryFactory
			.selectFrom(readLog)
			.where(readLog.user.id.eq(userId).and(readLog.date.eq(date)))
			.fetchOne();

		if (existingLog != null) {
			if (existingLog.getReadingTime() < minutes) {
				queryFactory
					.update(readLog)
					.set(readLog.readingTime, minutes)
					.where(readLog.user.id.eq(userId).and(readLog.date.eq(date)))
					.execute();
			}
		} else {
			ReadLog newReadLog = readLogMapper.toEntity(userId, date, minutes, getLatestStreak(userId, date));
			entityManager.persist(newReadLog);
		}
	} // 사용자의 읽기 기록을 저장하거나 업데이트

	private int getLatestStreak(Long userId, LocalDate date) {
		LocalDate yesterday = date.minusDays(1);

		Integer yesterdayStreak = queryFactory
			.select(readLog.streak)
			.from(readLog)
			.where(readLog.user.id.eq(userId).and(readLog.date.eq(yesterday)))
			.fetchOne();

		return (yesterdayStreak != null) ? yesterdayStreak + 1 : 1;
	} // 사용자의 연속 읽기 기록을 조회

	@Override
	public void resetStreakForInactiveUsers(LocalDate yesterday, LocalDate today) {
		int batchSize = 1000;
		long affectedRows;

		do {
			List<Long> userIds = fetchInactiveUserIds(yesterday, today, batchSize);
			affectedRows = userIds.isEmpty() ? 0 : resetStreakForUsers(yesterday, userIds);
		} while (affectedRows == batchSize);
	} // 비활성 사용자의 연속 읽기 기록을 리셋

	private List<Long> fetchInactiveUserIds(LocalDate yesterday, LocalDate today, int batchSize) {
		QReadLog readLogYesterday = new QReadLog("readLogYesterday");
		QReadLog readLogToday = new QReadLog("readLogToday");

		return queryFactory
			.select(readLogYesterday.user.id)
			.from(readLogYesterday)
			.leftJoin(readLogToday)
			.on(readLogYesterday.user.id
				.eq(readLogToday.user.id)
				.and(readLogToday.date.eq(today)))
			.where(readLogYesterday.date.eq(yesterday)
				.and(readLogToday.id.isNull()))
			.limit(batchSize)
			.fetch();
	} // 비활성 사용자들의 ID 목록을 조회하는 메소드

	private long resetStreakForUsers(LocalDate yesterday, List<Long> userIds) {
		return queryFactory.
			update(readLog)
			.set(readLog.streak, 0)
			.where(readLog.user.id.in(userIds)
				.and(readLog.date.eq(yesterday)))
			.execute();
	} // 사용자들의 연속 읽기 기록을 리셋
}