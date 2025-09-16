package com.sjy.LitHub.account.repository.follow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QFollow;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.model.res.FollowListResponseDTO;
import com.sjy.LitHub.file.entity.QUserGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QFollow follow = QFollow.follow;
	private final QUser user = QUser.user;
	private final QUserGenFile file = QUserGenFile.userGenFile;

	@Override
	public boolean existsByFollowerAndFollowee(Long followerId, Long followeeId) {
		return queryFactory
			.selectOne()
			.from(follow)
			.where(
				follow.follower.id.eq(followerId),
				follow.followee.id.eq(followeeId)
			)
			.fetchFirst() != null;
	}

	@Override
	public Page<FollowListResponseDTO> findFollowingsByUserId(Long userId, Pageable pageable) {
		List<FollowListResponseDTO> content = queryFactory
			.select(Projections.constructor(
				FollowListResponseDTO.class,
				user.id,
				user.nickName,
				file.storageKey,
				user.tier,
				user.point
			))
			.from(follow)
			.join(follow.followee, user)
			.leftJoin(user.userGenFiles, file)
			.on(file.typeCode.eq(UserGenFile.TypeCode.PROFILE_256))
			.where(follow.follower.id.eq(userId))
			.orderBy(follow.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(follow.count())
			.from(follow)
			.where(follow.follower.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	@Override
	public Page<FollowListResponseDTO> findFollowersByUserId(Long userId, Pageable pageable) {
		List<FollowListResponseDTO> content = queryFactory
			.select(Projections.constructor(
				FollowListResponseDTO.class,
				user.id,
				user.nickName,
				JPAExpressions
					.select(file.storageKey)
					.from(file)
					.where(file.user.id.eq(user.id),
						file.typeCode.eq(UserGenFile.TypeCode.PROFILE_256))
					.limit(1),
				user.tier,
				user.point
			))
			.from(follow)
			.join(follow.follower, user)
			.leftJoin(user.userGenFiles, file)
			.on(file.typeCode.eq(UserGenFile.TypeCode.PROFILE_256))
			.where(follow.followee.id.eq(userId))
			.orderBy(follow.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(follow.count())
			.from(follow)
			.where(follow.followee.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	@Override
	public int deleteByFollowerAndFollowee(Long followerId, Long followeeId) {
		return (int) queryFactory
			.delete(follow)
			.where(
				follow.follower.id.eq(followerId),
				follow.followee.id.eq(followeeId)
			)
			.execute();
	}

	@Override
	public Map<Long, Long> countFollowersByFolloweeIds(Collection<Long> followeeIds) {
		if (followeeIds == null || followeeIds.isEmpty()) return Map.of();

		return queryFactory
			.select(user.id, user.followerCount)
			.from(user)
			.where(user.id.in(followeeIds))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				row -> row.get(user.id),
				row -> Optional.ofNullable(row.get(user.followerCount)).orElse(0L)
			));
	}
}