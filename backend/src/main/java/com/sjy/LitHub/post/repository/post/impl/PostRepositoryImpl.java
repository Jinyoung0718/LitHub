package com.sjy.LitHub.post.repository.post.impl;

import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjy.LitHub.account.entity.QUser;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.post.entity.QComment;
import com.sjy.LitHub.post.entity.QLikes;
import com.sjy.LitHub.post.entity.QPost;
import com.sjy.LitHub.post.entity.QScrap;
import com.sjy.LitHub.post.model.res.PostDetailResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepositoryCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QPost post = QPost.post;
	private final QUser user = QUser.user;
	private final QLikes likes = QLikes.likes;
	private final QScrap scrap = QScrap.scrap;
	private final QComment comment = QComment.comment;

	@Override
	public Optional<PostDetailResponseDTO> findDetailDtoById(Long postId, Long currentUserId) {
		PostDetailResponseDTO result = queryFactory
			.select(Projections.constructor(PostDetailResponseDTO.class,
				post.id,
				post.title,
				post.contentMarkdown,
				post.images.get(PostGenFile.TypeCode.THUMBNAIL).filePath,
				user.nickName,
				user.userGenFiles.get(UserGenFile.TypeCode.PROFILE_256).filePath,
				likes.countDistinct(),
				scrap.countDistinct(),
				comment.countDistinct(),
				JPAExpressions
					.selectOne()
					.from(likes)
					.where(likes.user.id.eq(currentUserId), likes.post.id.eq(postId))
					.exists(),
				JPAExpressions
					.selectOne()
					.from(scrap)
					.where(scrap.user.id.eq(currentUserId), scrap.post.id.eq(postId))
					.exists(),
				post.user.id.eq(currentUserId),
				post.createdAt
			))
			.from(post)
			.join(post.user, user)
			.leftJoin(post.likes, likes)
			.leftJoin(post.scraps, scrap)
			.leftJoin(post.comments, comment)
			.where(post.id.eq(postId))
			.groupBy(post.id, user.id)
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
