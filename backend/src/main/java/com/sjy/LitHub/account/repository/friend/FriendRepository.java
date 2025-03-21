package com.sjy.LitHub.account.repository.friend;

import com.sjy.LitHub.account.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {

	@Modifying
	@Query("DELETE FROM Friend f WHERE f.requester.id = :userId OR f.receiver.id = :userId")
	void deleteAllByUserId(@Param("userId") Long userId);
}