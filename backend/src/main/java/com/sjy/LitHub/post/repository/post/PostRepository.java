package com.sjy.LitHub.post.repository.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostSortCustom, PostRepositoryCustom, PostSearchCustom {

	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.user.id = :userId AND p.deleted = false")
	Optional<Post> findByIdAndUserId(Long postId, Long userId);

	@Modifying
	@Query("UPDATE Post p SET p.deleted = true WHERE p.id = :postId AND p.user.id = :userId")
	int deletePost(Long postId, Long userId);
}