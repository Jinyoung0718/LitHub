package com.sjy.LitHub.post.repository.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.post.entity.QPostTag;
import com.sjy.LitHub.post.entity.QTag;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostTagRepositoryImpl implements PostTagRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QPostTag postTag = QPostTag.postTag;
	private final QTag tag = QTag.tag;

	@Override
	public Map<Long, List<String>> findTagNamesMap(List<Long> postIds) {
		List<Tuple> results = queryFactory
			.select(postTag.post.id, tag.name)
			.from(postTag)
			.join(postTag.tag, tag)
			.where(postTag.post.id.in(postIds))
			.fetch();

		Map<Long, List<String>> map = new HashMap<>();
		for (Tuple tuple : results) {
			Long postId = tuple.get(postTag.post.id);
			String tagName = tuple.get(tag.name);
			map.computeIfAbsent(postId, k -> new ArrayList<>()).add(tagName);
		}
		return map;
	}
}