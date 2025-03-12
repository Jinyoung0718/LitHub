package com.sjy.LitHub.record.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.record.QReadLog;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReadLogRepositoryImpl implements ReadLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReadLog readLog = QReadLog.readLog;

    @Override
    @Transactional(readOnly = true)
    public Integer getReadingStreak(Long userId) {
        return queryFactory
                .select(readLog.streak.max())
                .from(readLog)
                .where(readLog.user.id.eq(userId))
                .fetchOne();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, MonthlyReadingStatsResponseDTO> getMonthlyStatsBatch(Long userId, int year) {
        List<Tuple> results = queryFactory
                .select(readLog.date.month(), readLog.readingTime.sum(), readLog.readingTime.avg())
                .from(readLog)
                .where(readLog.user.id.eq(userId)
                        .and(readLog.date.between(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))))
                .groupBy(readLog.date.month())
                .fetch();

        results.sort(Comparator.comparing(tuple -> Optional.ofNullable(tuple.get(1, Integer.class)).orElse(0), Comparator.reverseOrder()));
        Integer mostFrequentReadingMonth = results.isEmpty() ? 0 : Optional.ofNullable(results.get(0).get(0, Integer.class)).orElse(0);

        return results.stream().collect(Collectors.toMap(
                tuple -> Optional.ofNullable(tuple.get(0, Integer.class)).orElse(0),
                tuple -> new MonthlyReadingStatsResponseDTO(
                        year,
                        Optional.ofNullable(tuple.get(0, Integer.class)).orElse(0),
                        Optional.ofNullable(tuple.get(1, Integer.class)).orElse(0),
                        Optional.ofNullable(tuple.get(2, Double.class)).orElse(0.0),
                        Optional.ofNullable(tuple.get(0, Integer.class)).map(month -> month.equals(mostFrequentReadingMonth)).orElse(false)
                )
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadingRecordResponseDTO> getReadingRecords(Long userId, int year) {
        return queryFactory
                .select(Projections.constructor(ReadingRecordResponseDTO.class,
                        readLog.date,
                        Expressions.cases()
                                .when(readLog.readingTime.between(1, 10)).then(1)
                                .when(readLog.readingTime.between(11, 30)).then(2)
                                .when(readLog.readingTime.between(31, 60)).then(3)
                                .when(readLog.readingTime.between(61, 120)).then(4)
                                .otherwise(5)
                ))
                .from(readLog)
                .where(readLog.user.id.eq(userId)
                        .and(readLog.date.between(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))))
                .fetch();
    }

    @Override
    @Transactional
    public void upsertReadingLog(Long userId, LocalDate date, int minutes) {
        long updatedRows = queryFactory
                .update(readLog)
                .set(readLog.readingTime, Expressions.numberTemplate(Integer.class, "GREATEST({0}, {1})", readLog.readingTime, minutes))
                .where(readLog.user.id.eq(userId).and(readLog.date.eq(date)))
                .execute();

        if (updatedRows == 0) {
            int newStreak = getUpdatedStreak(userId);
            queryFactory
                    .insert(readLog)
                    .columns(readLog.user.id, readLog.date, readLog.readingTime, readLog.streak)
                    .values(userId, date, minutes, newStreak)
                    .execute();
        } else {
            queryFactory
                    .update(readLog)
                    .set(readLog.streak, Expressions.numberTemplate(Integer.class, "GREATEST({0}, {1})", readLog.streak, 1)) // 연속일 수는 1로 갱신
                    .where(readLog.user.id.eq(userId).and(readLog.date.eq(date)))
                    .execute();
        }
    }


    @Override
    @Transactional
    public void resetStreakForInactiveUsers(LocalDate yesterday, LocalDate today) {
        int batchSize = 1000;
        long affectedRows;

        do {
            List<Long> userIds = queryFactory.select(readLog.user.id)
                    .from(readLog)
                    .where(readLog.date.eq(yesterday)
                            .and(readLog.user.id.notIn(
                                    JPAExpressions.select(readLog.user.id)
                                            .from(readLog)
                                            .where(readLog.date.eq(today))
                            )))
                    .limit(batchSize)
                    .fetch();

            affectedRows = queryFactory.update(readLog)
                    .set(readLog.streak, 0)
                    .where(readLog.user.id.in(userIds)
                            .and(readLog.date.eq(yesterday)))
                    .execute();
        } while (affectedRows == batchSize);
    }

    private int getUpdatedStreak(Long userId) {
        Integer latestStreak = queryFactory
                .select(readLog.streak)
                .from(readLog)
                .where(readLog.user.id.eq(userId))
                .orderBy(readLog.date.desc())
                .limit(1)
                .fetchOne();
        return (latestStreak != null ? latestStreak : 0) + 1;
    }
}