package com.sjy.LitHub.account.repository.user.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QUser user = QUser.user;

	@Override
	public UserProfileResponseDTO getUserProfile(Long userId) {
		User user = queryFactory.selectFrom(QUser.user)
			.leftJoin(QUser.user.userGenFiles).fetchJoin()
			.where(QUser.user.id.eq(userId))
			.fetchOne();

		if (user == null) {
			throw new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND);
		}

		return UserProfileResponseDTO.builder()
			.userId(user.getId())
			.nickname(user.getNickName())
			.profileImageUrlLarge(user.getProfileImageUrl512())
			.tier(user.getTier())
			.point(user.getPoint())
			.build();
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