package com.sjy.LitHub.account.repository.friend;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.Friend;
import com.sjy.LitHub.account.entity.QFriend;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.account.entity.User;
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

	// 친구 목록 조회
	@Override
	public List<FriendListResponseDTO> findAcceptedFriendsByUserId(Long userId) {
		List<Friend> friends = queryFactory
			.selectFrom(friend)
			.join(friend.requester, user1).fetchJoin()
			.join(friend.receiver, user2).fetchJoin()
			.leftJoin(user1.userGenFiles).fetchJoin()
			.leftJoin(user2.userGenFiles).fetchJoin()
			.where(friend.status.eq(FriendStatus.ACCEPTED),
				user1.deletedAt.isNull(),
				user2.deletedAt.isNull(),
				friend.requester.id.eq(userId).or(friend.receiver.id.eq(userId)))
			.fetch();

		return friends.stream()
			.map(f -> {
				User target = f.getRequester().getId().equals(userId) ? f.getReceiver() : f.getRequester();
				return FriendListResponseDTO.of(target);
			})
			.collect(Collectors.toList());
	}

	// 친구 요청 목록 조회
	@Override
	public List<FriendRequestResponseDTO> findPendingFriendRequests(Long userId) {
		List<Friend> friends = queryFactory
			.selectFrom(friend)
			.join(friend.requester, user1).fetchJoin()
			.leftJoin(user1.userGenFiles).fetchJoin()
			.where(friend.receiver.id.eq(userId),
				friend.status.eq(FriendStatus.PENDING))
			.orderBy(friend.createdAt.desc())
			.fetch();

		return friends.stream()
			.map(FriendRequestResponseDTO::of)
			.collect(Collectors.toList());
	}
}