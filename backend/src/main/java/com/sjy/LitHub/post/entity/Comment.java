package com.sjy.LitHub.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment", indexes = {
	@Index(name = "idx_comment_user_id", columnList = "user_id"),
	@Index(name = "idx_comment_post_created", columnList = "post_id, created_at"), // 인덱스 레인지 스캔
	@Index(name = "idx_comment_parent_id", columnList = "parent_id")
})
public class Comment extends BaseTime {

	@Lob
	@Setter
	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private int depth; // 0: 댓글, 1: 대댓글

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Comment> children = new ArrayList<>();
}