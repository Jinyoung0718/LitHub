package com.sjy.LitHub.account.repository.user.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QUser user = QUser.user;

	@Override
	public UserProfileResponseDTO getUserProfile(Long userId) {
		return queryFactory
			.select(Projections.constructor(UserProfileResponseDTO.class,
				user.id,
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
	public boolean updateNickNameIfNotExists(Long userId, String newNickName) {
		Long existingNickNameCount = queryFactory
			.select(user.count())
			.from(user)
			.where(user.nickName.eq(newNickName).and(user.id.ne(userId)))
			.fetchOne();

		if (existingNickNameCount != null && existingNickNameCount > 0) {
			return false;
		}

		long updatedRows = queryFactory
			.update(user)
			.set(user.nickName, newNickName)
			.where(user.id.eq(userId))
			.execute();

		return updatedRows > 0;
	}
}