package com.sjy.LitHub.post.repository.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Modifying
	@Query("DELETE FROM Comment c WHERE c.id = :commentId AND c.user.id = :userId")
	int deleteByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

	@Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.user.id = :userId")
	Optional<Comment> findByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

	// 루트 댓글
	Page<Comment> findByPostIdAndDepthOrderByCreatedAtAsc(Long postId, int depth, Pageable pageable);

	// 특정 루트 댓글의 대댓글
	Page<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);

	int countByParentId(Long parentId);
}