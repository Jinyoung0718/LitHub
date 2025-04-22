package com.sjy.LitHub.post.repository.post.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.post.entity.QPost;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostSearchCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostSearchCustomImpl implements PostSearchCustom {

	private final JPAQueryFactory queryFactory;

	private final QPost post = QPost.post;
	private final QUser user = QUser.user;

	private Page<PostSummaryResponseDTO> executeFullTextQuery(NumberTemplate<Double> matchExpr, Pageable pageable) {
		List<PostSummaryResponseDTO> results = queryFactory
			.select(Projections.constructor(PostSummaryResponseDTO.class,
				post.id,
				post.title,
				user.id,
				user.nickName,
				Expressions.nullExpression(Long.class),
				Expressions.nullExpression(Long.class),
				Expressions.nullExpression(Long.class),
				post.createdAt,
				Expressions.nullExpression(String.class),
				Expressions.nullExpression(String.class)
			))
			.from(post)
			.join(post.user, user)
			.where(matchExpr.gt(0))
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(post.count())
			.from(post)
			.where(matchExpr.gt(0))
			.fetchOne();

		return new PageImpl<>(results, pageable, total != null ? total : 0);
	}

	@Override
	public Page<PostSummaryResponseDTO> searchByTitle(String keyword, Pageable pageable) {
		NumberTemplate<Double> matchExpr = Expressions.numberTemplate(
			Double.class,
			"function('match_against', {0}, {1})",
			post.title, keyword + "*"
		);
		return executeFullTextQuery(matchExpr, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> searchByContent(String keyword, Pageable pageable) {
		NumberTemplate<Double> matchExpr = Expressions.numberTemplate(
			Double.class,
			"function('match_against', {0}, {1})",
			post.searchContent, keyword + "*"
		);
		return executeFullTextQuery(matchExpr, pageable);
	}

	@Override
	public Page<PostSummaryResponseDTO> searchByTitleOrContent(String keyword, Pageable pageable) {
		NumberTemplate<Double> matchExpr = Expressions.numberTemplate(
			Double.class,
			"function('match_against', {0}, {1}) + function('match_against', {2}, {3})",
			post.title, keyword + "*", post.searchContent, keyword + "*"
		);
		return executeFullTextQuery(matchExpr, pageable);
	}
}