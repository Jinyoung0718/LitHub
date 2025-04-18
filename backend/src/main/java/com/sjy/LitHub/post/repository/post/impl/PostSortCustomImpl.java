package com.sjy.LitHub.post.repository.post.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.post.entity.QComment;
import com.sjy.LitHub.post.entity.QLikes;
import com.sjy.LitHub.post.entity.QPost;
import com.sjy.LitHub.post.entity.QPostTag;
import com.sjy.LitHub.post.entity.QScrap;
import com.sjy.LitHub.post.entity.QTag;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostSortCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostSortCustomImpl implements PostSortCustom {

	private final JPAQueryFactory queryFactory;

	private final QPost post = QPost.post;
	private final QUser user = QUser.user;
	private final QPostTag postTag = QPostTag.postTag;
	private final QTag tag = QTag.tag;
	private final QLikes likes = QLikes.likes;
	private final QScrap scrap = QScrap.scrap;
	private final QComment comment = QComment.comment;

	@Override
	public Page<PostSummaryResponseDTO> searchByKeyword(String keyword, Pageable pageable) {
		BooleanExpression predicate = post.title.containsIgnoreCase(keyword);
		OrderSpecifier<?> order = post.createdAt.desc();
		return fetchPostPage(predicate, order, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable) {
		BooleanExpression predicate = tag.name.eq(tagName);
		OrderSpecifier<?> order = post.createdAt.desc();
		return fetchPostPage(predicate, order, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> findPopularPosts(Pageable pageable) {
		BooleanExpression predicate = post.createdAt.after(LocalDateTime.now().minusDays(1));
		OrderSpecifier<?> order = likes.count().add(scrap.count()).desc();
		return fetchPostPage(predicate, order, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> findMyPosts(Long userId, Pageable pageable) {
		BooleanExpression predicate = post.user.id.eq(userId);
		OrderSpecifier<?> order = post.createdAt.desc();
		return fetchPostPage(predicate, order, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> findPostsScrappedByUser(Long userId, Pageable pageable) {
		BooleanExpression predicate = scrap.user.id.eq(userId);
		OrderSpecifier<?> order = post.createdAt.desc();
		return fetchPostPage(predicate, order, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> findFollowerFeedsByPriority(List<Long> followeeIds, Pageable pageable) {
		LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
		BooleanExpression predicate = post.user.id.in(followeeIds).and(post.createdAt.after(monthAgo));

		OrderSpecifier<?> order = likes.count().multiply(3)
			.add(scrap.count().multiply(2))
			.add(comment.count()).desc();

		return fetchPostPage(predicate, order, pageable);
	}

	private Page<PostSummaryResponseDTO> fetchPostPage(BooleanExpression whereCondition, OrderSpecifier<?> orderBy, Pageable pageable) {
		List<PostSummaryResponseDTO> contents = queryFactory
			.select(Projections.constructor(PostSummaryResponseDTO.class,
				post.id,
				post.title,
				user.id,
				new CaseBuilder()
					.when(user.deletedAt.isNotNull()).then("탈퇴한 사용자")
					.otherwise(user.nickName),

				Expressions.nullExpression(Long.class),
				Expressions.nullExpression(Long.class),
				Expressions.nullExpression(Long.class),
				post.createdAt,

				Expressions.nullExpression(String.class),
				Expressions.nullExpression(String.class)
			))
			.from(post)
			.join(post.user, user)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(whereCondition)
			.groupBy(post.id, user.id)
			.orderBy(orderBy)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(post.countDistinct())
			.from(post)
			.leftJoin(post.postTags, postTag)
			.leftJoin(postTag.tag, tag)
			.where(whereCondition)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total != null ? total : 0);
	}
}