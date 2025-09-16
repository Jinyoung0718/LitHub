package com.sjy.LitHub.post.repository.scrap;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Scrap;
import com.sjy.LitHub.post.model.res.toggle.IdCount;
import com.sjy.LitHub.post.repository.InteractionCountRepository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long>, InteractionCountRepository {

	void deleteByPostIdAndUserId(Long postId, Long userId);

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	long countByPostId(Long postId);

	@Query("""
        select new com.sjy.LitHub.post.model.res.toggle.IdCount(s.post.id, count(s))
        from Scrap s
        where s.post.id in :postIds
        group by s.post.id
    """)
	List<IdCount> countByPostIdsRaw(@Param("postIds") Collection<Long> postIds);
}