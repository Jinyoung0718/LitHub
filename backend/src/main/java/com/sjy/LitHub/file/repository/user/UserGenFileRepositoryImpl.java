package com.sjy.LitHub.file.repository.user;

import java.util.List;
import java.util.Set;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.file.entity.QUserGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserGenFileRepositoryImpl implements UserGenFileRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QUserGenFile userGenFile = QUserGenFile.userGenFile;

	@Override
	public List<UserGenFile> findProfiles256ByUserIds(Set<Long> userIds) {
		return queryFactory.selectFrom(userGenFile)
			.where(userGenFile.user.id.in(userIds)
				.and(userGenFile.typeCode.eq(UserGenFile.TypeCode.PROFILE_256)))
			.fetch();
	}
}