package com.sjy.LitHub.account.repository.friend;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.Friend;
import com.sjy.LitHub.account.entity.QFriend;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.authenum.FriendStatus;
import com.sjy.LitHub.account.mapper.FriendMapper;
import com.sjy.LitHub.account.model.res.FriendListResponseDTO;
import com.sjy.LitHub.account.model.res.FriendRequestResponseDTO;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;
	private final FriendMapper friendMapper;
	private final QFriend friend = QFriend.friend;
	private final QUser user1 = new QUser("user1");
	private final QUser user2 = new QUser("user2");

	// 친구 요청 존재 여부 확인
	@Override
	public long insertIfNotExists(Long requesterId, Long receiverId) {
		boolean exists = queryFactory
			.selectOne()
			.from(friend)
			.where(friend.requester.id.eq(requesterId).and(friend.receiver.id.eq(receiverId)))
			.fetchFirst() != null;

		if (!exists) {
			exists = queryFactory
				.selectOne()
				.from(friend)
				.where(friend.requester.id.eq(receiverId).and(friend.receiver.id.eq(requesterId)))
				.fetchFirst() != null;
		}

		if (exists) return 0;
		Friend newFriend = friendMapper.toEntity(requesterId, receiverId);
		entityManager.persist(newFriend);
		return 1;
	}

	// 친구 요청 수락
	@Override
	public long updateFriendStatusToAccepted(Long requestId) {
		return queryFactory.update(friend)
			.set(friend.status, FriendStatus.ACCEPTED)
			.where(friend.id.eq(requestId)
				.and(friend.status.eq(FriendStatus.PENDING)))
			.execute();
	}

	// 친구 요청 거절
	@Override
	public long deleteFriendRequest(Long requestId) {
		return queryFactory.delete(friend)
			.where(friend.id.eq(requestId))
			.execute();
	}

	// 친구 삭제
	@Override
	public long deleteFriend(Long friendId) {
		return queryFactory.delete(friend)
			.where(friend.id.eq(friendId))
			.execute();
	}

	// 친구 목록 조회
	@Override
	public List<FriendListResponseDTO> findAcceptedFriendsByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(FriendListResponseDTO.class,
				new CaseBuilder()
					.when(friend.requester.id.eq(userId)).then(user2.id)
					.otherwise(user1.id),
				new CaseBuilder()
					.when(friend.requester.id.eq(userId)).then(user2.nickName)
					.otherwise(user1.nickName),
				new CaseBuilder()
					.when(friend.requester.id.eq(userId)).then(user2.profileImageUrlSmall)
					.otherwise(user1.profileImageUrlSmall),
				new CaseBuilder()
					.when(friend.requester.id.eq(userId)).then(user2.tier)
					.otherwise(user1.tier),
				new CaseBuilder()
					.when(friend.requester.id.eq(userId)).then(user2.point)
					.otherwise(user1.point)
			))
			.from(friend)
			.join(user1).on(friend.requester.eq(user1))
			.join(user2).on(friend.receiver.eq(user2))
			.where(friend.requester.id.eq(userId)
					.or(friend.receiver.id.eq(userId)),
				friend.status.eq(FriendStatus.ACCEPTED),
				user1.deletedAt.isNull(),
				user2.deletedAt.isNull())
			.fetch();
	}

	// 친구 요청 목록 조회
	@Override
	public List<FriendRequestResponseDTO> findPendingFriendRequests(Long userId) {
		return queryFactory
			.select(Projections.constructor(FriendRequestResponseDTO.class,
				friend.id,
				user1.nickName,
				user1.profileImageUrlSmall,
				friend.createdAt
			))
			.from(friend)
			.join(user1).on(friend.requester.eq(user1))
			.where(friend.receiver.id.eq(userId),
				friend.status.eq(FriendStatus.PENDING))
			.orderBy(friend.createdAt.desc())
			.fetch();
	}
}