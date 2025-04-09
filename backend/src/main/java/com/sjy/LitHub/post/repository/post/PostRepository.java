package com.sjy.LitHub.post.repository.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostSortCustom, PostRepositoryCustom {

	@Query("SELECT p FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
	Optional<Post> findByIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

	@Modifying
	@Query("DELETE FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
	int deletePost(@Param("postId") Long postId, @Param("userId") Long userId);
}