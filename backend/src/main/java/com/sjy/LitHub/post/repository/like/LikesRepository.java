package com.sjy.LitHub.post.repository.like;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.Likes;
import com.sjy.LitHub.post.model.res.toggle.IdCount;
import com.sjy.LitHub.post.repository.InteractionCountRepository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long>, InteractionCountRepository {

	void deleteByPostIdAndUserId(Long postId, Long userId);

	boolean existsByPostIdAndUserId(Long postId, Long userId);

	long countByPostId(Long postId);

	@Query("""
        select new com.sjy.LitHub.post.model.res.toggle.IdCount(l.post.id, count(l))
        from Likes l
        where l.post.id in :postIds
        group by l.post.id
    """)
	List<IdCount> countByPostIdsRaw(@Param("postIds") Collection<Long> postIds);
}
