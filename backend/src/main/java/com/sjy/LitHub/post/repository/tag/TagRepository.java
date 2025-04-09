package com.sjy.LitHub.post.repository.tag;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sjy.LitHub.post.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	List<Tag> findAllByNameIn(Collection<String> names);

	@Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT(:keyword, '%'))")
	List<Tag> searchTop10ByKeyword(@Param("keyword") String keyword, Pageable pageable);
}