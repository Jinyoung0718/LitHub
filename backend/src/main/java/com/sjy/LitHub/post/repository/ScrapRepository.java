package com.sjy.LitHub.post.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	@Modifying
	@Query("DELETE FROM Scrap s WHERE s.post.id = :postId AND s.user.id NOT IN :userIds")
	void deleteUsersNotInScraps(@Param("postId") Long postId, @Param("userIds") Set<Long> userIds);

	default void syncScraps(Long postId, Set<Long> userIds) {
		deleteUsersNotInScraps(postId, userIds);
	}

	long countByPostId(Long postId);
}