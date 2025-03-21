package com.sjy.LitHub.account.repository.point;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.authenum.Tier;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    public void updateUserPointsAndTier(Long userId, int minutes) {
        Integer result = queryFactory
            .select(user.point.add(minutes))
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne();

        int newPoints = result != null ? result : 0;

        queryFactory.update(user)
            .set(user.point, newPoints)
            .set(user.tier, Tier.getTierByPoints(newPoints))
            .where(user.id.eq(userId))
            .execute();
    }
}