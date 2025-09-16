package com.sjy.LitHub.post.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sjy.LitHub.post.model.res.toggle.IdCount;

public interface InteractionCountRepository {
	List<IdCount> countByPostIdsRaw(Collection<Long> postIds);

	default Map<Long, Long> countByPostIds(Collection<Long> postIds) {
		if (postIds == null || postIds.isEmpty()) {
			return Map.of();
		}

		Map<Long, Long> m = countByPostIdsRaw(postIds).stream()
			.collect(Collectors.toMap(IdCount::postId, IdCount::cnt));

		postIds.forEach(id -> m.putIfAbsent(id, 0L));
		return m;
	}
}