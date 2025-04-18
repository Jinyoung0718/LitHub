package com.sjy.LitHub.file.repository.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.QPostGenFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostGenFileRepositoryImpl implements PostGenFileRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QPostGenFile postGenFile = QPostGenFile.postGenFile;

	@Override
	public List<PostGenFile> findUnusedImagesByTypeBefore(PostGenFile.TypeCode typeCode, LocalDateTime timeLimit) {
		return queryFactory.selectFrom(postGenFile)
			.where(
				postGenFile.post.isNull(),
				postGenFile.typeCode.eq(typeCode),
				postGenFile.createdAt.before(timeLimit)
			)
			.fetch();
	}

	@Override
	public Optional<PostGenFile> findThumbnailByPostId(Long postId) {
		return Optional.ofNullable(queryFactory
			.selectFrom(postGenFile)
			.where(postGenFile.post.id.eq(postId)
				.and(postGenFile.typeCode.eq(PostGenFile.TypeCode.THUMBNAIL)))
			.fetchOne());
	}

	@Override
	public List<PostGenFile> findMarkdownImagesByPostId(Long postId) {
		return queryFactory
			.selectFrom(postGenFile)
			.where(postGenFile.post.id.eq(postId)
				.and(postGenFile.typeCode.eq(PostGenFile.TypeCode.MARKDOWN)))
			.fetch();
	}

	@Override
	public List<PostGenFile> findThumbnailsByPostIds(List<Long> postIds) {
		return queryFactory.selectFrom(postGenFile)
			.where(postGenFile.post.id.in(postIds)
				.and(postGenFile.typeCode.eq(PostGenFile.TypeCode.THUMBNAIL)))
			.fetch();
	}
}