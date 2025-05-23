package com.sjy.LitHub.post.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.Entity;
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
@Table(name = "scrap", indexes = {
	@Index(name = "idx_scrap_post_id", columnList = "post_id"), // 특정 게시글의 스크랩 수 조회용
	@Index(name = "idx_scrap_user_post", columnList = "user_id, post_id") // 유저가 해당 게시글을 스크랩했는지 여부 확인용
})
public class Scrap extends BaseTime {

	@ManyToOne
	@JoinColumn(nullable = false)
	private Post post;

	@ManyToOne
	@JoinColumn(nullable = false)
	private User user;

}