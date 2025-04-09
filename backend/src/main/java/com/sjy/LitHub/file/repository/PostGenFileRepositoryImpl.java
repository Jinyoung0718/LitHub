package com.sjy.LitHub.file.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.QPostGenFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostGenFileRepositoryImpl implements PostGenFileRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QPostGenFile postGenFile = QPostGenFile.postGenFile;

	@Override
	public List<PostGenFile> findUnusedMarkdownImagesBefore(LocalDateTime timeLimit) {
		return queryFactory.selectFrom(postGenFile)
			.where(
				postGenFile.post.isNull(),
				postGenFile.typeCode.eq(PostGenFile.TypeCode.MARKDOWN),
				postGenFile.createdAt.before(timeLimit)
			)
			.fetch();
	}

	@Override
	public List<PostGenFile> findTemporaryMarkdownImagesByUser(Long userId) {
		return queryFactory.selectFrom(postGenFile)
			.where(
				postGenFile.post.isNull(),
				postGenFile.user.id.eq(userId),
				postGenFile.typeCode.eq(PostGenFile.TypeCode.MARKDOWN)
			)
			.fetch();
	}
}