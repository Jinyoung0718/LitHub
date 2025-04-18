package com.sjy.LitHub.account.repository.follow;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
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
				file.filePath,
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
				file.filePath,
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
	public List<Long> findFolloweeIdsByUserId(Long userId) {
		return queryFactory
			.select(follow.followee.id)
			.from(follow)
			.where(follow.follower.id.eq(userId))
			.fetch();
	}
}