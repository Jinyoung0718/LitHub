package com.sjy.LitHub.account.repository.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(Long userId) {
        return queryFactory
                .select(Projections.constructor(UserProfileResponseDTO.class,
                        user.userEmail,
                        user.nickName,
                        user.profileImageUrlLarge,
                        user.tier,
                        user.point
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    @Override
    @Transactional
    public boolean updateNickNameIfNotExists(Long userId, String newNickName) {
        Long existingNickNameCount = queryFactory
                .select(user.count())
                .from(user)
                .where(user.nickName.eq(newNickName).and(user.id.ne(userId)))
                .fetchOne();

        if (existingNickNameCount != null && existingNickNameCount > 0) {
            return false;
        }

        long updatedRows = queryFactory.update(user)
                .set(user.nickName, newNickName)
                .where(user.id.eq(userId))
                .execute();

        return updatedRows > 0;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId, LocalDateTime deletedAt) {
        Long count = queryFactory
                .select(user.count())
                .from(user)
                .where(user.id.eq(userId).and(user.deletedAt.isNull()))
                .fetchOne();

        if (count == null || count == 0) {
            throw new InvalidUserException(BaseResponseStatus.USER_ALREADY_DELETED);
        }

        queryFactory.update(user)
                .set(user.deletedAt, deletedAt)
                .where(user.id.eq(userId).and(user.deletedAt.isNull()))
                .execute();
    }
}