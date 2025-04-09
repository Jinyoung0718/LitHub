package com.sjy.LitHub.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Modifying
	@Query("DELETE FROM Comment c WHERE c.id = :commentId AND c.user.id = :userId")
	int deleteByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

	@Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.user.id = :userId")
	Optional<Comment> findByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
}