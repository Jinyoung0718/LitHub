package com.sjy.LitHub.account.repository.point;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.authenum.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    @Transactional
    public void updateUserPointsAndTier(Long userId, int minutes) {
        Integer updatedPoints = queryFactory
                .select(user.point.add(minutes))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (updatedPoints != null) {
            Tier newTier = Tier.getTierByPoints(updatedPoints);

            queryFactory
                    .update(user)
                    .set(user.point, updatedPoints)
                    .set(user.tier, newTier)
                    .where(user.id.eq(userId))
                    .execute();
        }
    }
}