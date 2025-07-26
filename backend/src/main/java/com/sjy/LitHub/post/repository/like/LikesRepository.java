package com.sjy.LitHub.post.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Likes;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	void deleteByPostIdAndUserId(Long postId, Long userId);

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	long countByPostId(Long postId);
}