package com.sjy.LitHub.post.repository.post.impl;

import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.post.entity.QComment;
import com.sjy.LitHub.post.entity.QPost;
import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepositoryCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QPost post = QPost.post;
	private final QUser user = QUser.user;
	private final QComment comment = QComment.comment;

	@Override
	public Optional<PostDetailResponseDTO> findDetailDtoById(Long postId, Long currentUserId) {

		PostDetailResponseDTO result = queryFactory
			.select(Projections.constructor(PostDetailResponseDTO.class,
				post.id,
				post.title,
				post.contentMarkdown,
				user.nickName,

				Expressions.nullExpression(Long.class),
				Expressions.nullExpression(Long.class),

				JPAExpressions
					.select(comment.count())
					.from(comment)
					.where(comment.post.id.eq(post.id)),

				Expressions.nullExpression(Boolean.class),
				Expressions.nullExpression(Boolean.class),
				post.user.id.eq(currentUserId),

				post.createdAt,

				Expressions.nullExpression(String.class),
				Expressions.nullExpression(String.class)
			))
			.from(post)
			.join(post.user, user)
			.where(post.id.eq(postId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}