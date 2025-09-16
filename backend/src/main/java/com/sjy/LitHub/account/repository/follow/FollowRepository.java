package com.sjy.LitHub.account.repository.follow;

import java.util.List;

import com.sjy.LitHub.account.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	@Query("SELECT f.follower.id FROM Follow f WHERE f.followee.id = :userId")
	List<Long> findFollowerIdsByUserId(Long userId);

	long countByFolloweeId(Long userId);
}