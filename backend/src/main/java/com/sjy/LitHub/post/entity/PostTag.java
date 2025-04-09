package com.sjy.LitHub.post.entity;

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

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_tag", indexes = {
	@Index(name = "idx_post_tag_post_id", columnList = "post_id"),
	@Index(name = "idx_post_tag_tag_id", columnList = "tag_id")
})
public class PostTag extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@Setter
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Tag tag;

	public static PostTag of(Post post, Tag tag) {
		PostTag postTag = PostTag.builder()
			.tag(tag)
			.build();

		postTag.setPost(post);
		return postTag;
	}
}