package com.sjy.LitHub.account.repository.user.porfile;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.QUserGenFile;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QUser user = QUser.user;
	private final QUserGenFile userGenFile = QUserGenFile.userGenFile;

	@Override
	public Optional<User> findUserWithGenFilesById(Long userId) {

		return Optional.ofNullable(
			queryFactory
				.selectFrom(user)
				.leftJoin(user.userGenFiles, userGenFile).fetchJoin()
				.where(user.id.eq(userId), user.deletedAt.isNull())
				.fetchOne()
		);
	}
}