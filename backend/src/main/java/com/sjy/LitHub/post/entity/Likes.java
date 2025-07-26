package com.sjy.LitHub.post.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "likes", indexes = {
	@Index(name = "idx_likes_post_id", columnList = "post_id"), // 특정 게시글의 좋아요 수 조회용
	@Index(name = "idx_likes_user_post", columnList = "user_id, post_id") // 유저가 해당 게시글을 좋아요 눌렀는지 여부 확인용
})
public class Likes extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	public static Likes of(Post post, User user) {
		return Likes.builder()
			.post(post)
			.user(user)
			.build();
	}
}