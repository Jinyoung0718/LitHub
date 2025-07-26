package com.sjy.LitHub.post.repository.scrap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Scrap;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

	void deleteByPostIdAndUserId(Long postId, Long userId);

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	long countByPostId(Long postId);
}