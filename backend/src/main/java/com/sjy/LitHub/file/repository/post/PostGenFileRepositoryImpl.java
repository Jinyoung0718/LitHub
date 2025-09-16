package com.sjy.LitHub.file.repository.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.QPostGenFile;

import io.jsonwebtoken.lang.Collections;
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

	@Override
	public List<PostGenFile> findMarkdownImagesToDelete(Long postId, List<String> usedFileNames) {
		return queryFactory
			.selectFrom(postGenFile)
			.where(
				postGenFile.post.id.eq(postId),
				postGenFile.typeCode.eq(PostGenFile.TypeCode.MARKDOWN),
				Expressions.stringTemplate(
					"CONCAT({0}, '.', {1})",
					postGenFile.fileNo, postGenFile.fileExt
				).notIn(usedFileNames)
			)
			.fetch();
	}

	@Override
	public List<PostGenFile> findMarkdownImagesOrdered(Long userId, List<String> usedFileNames) {
		if (usedFileNames == null || usedFileNames.isEmpty()) {
			return Collections.emptyList();
		}

		CaseBuilder.Cases<Integer, NumberExpression<Integer>> caseBuilder =
			new CaseBuilder().when(
				Expressions.stringTemplate("CONCAT({0}, '.', {1})", postGenFile.fileNo, postGenFile.fileExt)
					.eq(usedFileNames.get(0))
			).then(1);

		for (int i = 1; i < usedFileNames.size(); i++) {
			caseBuilder = caseBuilder.when(
				Expressions.stringTemplate("CONCAT({0}, '.', {1})", postGenFile.fileNo, postGenFile.fileExt)
					.eq(usedFileNames.get(i))
			).then(i + 1);
		}

		NumberExpression<Integer> orderCase = caseBuilder.otherwise(Integer.MAX_VALUE);

		return queryFactory
			.selectFrom(postGenFile)
			.where(
				postGenFile.user.id.eq(userId),
				postGenFile.typeCode.eq(PostGenFile.TypeCode.MARKDOWN),
				Expressions.stringTemplate("CONCAT({0}, '.', {1})", postGenFile.fileNo, postGenFile.fileExt)
					.in(usedFileNames)
			)
			.orderBy(orderCase.asc())
			.fetch();
	}
}