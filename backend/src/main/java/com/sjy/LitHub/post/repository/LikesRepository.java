package com.sjy.LitHub.post.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Likes;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	@Modifying
	@Query("DELETE FROM Likes l WHERE l.post.id = :postId AND l.user.id NOT IN :userIds")
	void deleteUsersNotInLikes(@Param("postId") Long postId, @Param("userIds") Set<Long> userIds);

	default void syncLikes(Long postId, Set<Long> userIds) {
		deleteUsersNotInLikes(postId, userIds);
	}

	long countByPostId(Long postId);
}