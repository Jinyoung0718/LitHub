package com.sjy.LitHub.post.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Likes;
import com.sjy.LitHub.post.entity.Post;

public interface LikesRepository extends JpaRepository<Likes, Long> {

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	@Modifying
	@Query("DELETE FROM Likes l WHERE l.post.id = :postId AND l.user.id = :userId")
	void toggleLikeIfExists(@Param("postId") Long postId, @Param("memberId") Long memberId);

	long countByPostId(Long postId);

	@Query("SELECT l.post FROM Likes l WHERE l.user.id = :userId")
	List<Post> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}