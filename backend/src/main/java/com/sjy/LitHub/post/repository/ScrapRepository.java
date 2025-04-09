package com.sjy.LitHub.post.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	@Modifying
	@Query("DELETE FROM Scrap s WHERE s.post.id = :postId AND s.user.id = :userId")
	void toggleScrapIfExists(@Param("postId") Long postId, @Param("userId") Long userId);

	long countByPostId(Long postId);

	@Query("SELECT s.post FROM Scrap s WHERE s.user.id = :userId")
	List<Post> findScrappedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}