package com.sjy.LitHub.account.repository.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.account.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {

	// 친구 요청 수락
	@Modifying
	@Query("UPDATE Friend f SET f.status = 'ACCEPTED' WHERE f.id = :requestId AND f.status = 'PENDING'")
	long updateFriendStatusToAccepted(@Param("requestId") Long requestId);

	// 친구 요청 거절
	@Modifying
	@Query("DELETE FROM Friend f WHERE f.id = :requestId")
	long deleteFriendRequest(@Param("requestId") Long requestId);

	// 친구 삭제
	@Modifying
	@Query("DELETE FROM Friend f WHERE f.id = :friendId")
	long deleteFriend(@Param("friendId") Long friendId);
}