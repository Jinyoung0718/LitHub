package com.sjy.LitHub.post.repository.post.impl;

import static com.sjy.LitHub.post.entity.QLikes.*;
import static com.sjy.LitHub.post.entity.QScrap.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
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
				comment.countDistinct(),

				Expressions.nullExpression(Boolean.class),
				Expressions.nullExpression(Boolean.class),
				Expressions.nullExpression(Boolean.class),
				post.user.id.eq(currentUserId),

				post.createdAt,

				Expressions.nullExpression(String.class),
				Expressions.nullExpression(String.class)
			))
			.from(post)
			.join(post.user, user)
			.leftJoin(post.comments, comment)
			.where(post.id.eq(postId))
			.groupBy(post.id, user.id)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<Long> findTopPopularPostIds(Pageable pageable) {
		return queryFactory
			.select(post.id)
			.from(post)
			.leftJoin(post.likes, likes)
			.leftJoin(post.scraps, scrap)
			.where(post.createdAt.after(LocalDateTime.now().minusDays(1)))
			.groupBy(post.id)
			.orderBy(likes.count().add(scrap.count()).desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}