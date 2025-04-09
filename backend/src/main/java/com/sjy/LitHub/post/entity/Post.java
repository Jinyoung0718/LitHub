package com.sjy.LitHub.post.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", indexes = {
	@Index(name = "idx_post_title", columnList = "title"),
	@Index(name = "idx_post_user_id", columnList = "user_id"),
	@Index(name = "idx_post_user_created", columnList = "user_id, created_at")
})
public class Post extends BaseTime {

	@Column(nullable = false, length = 200)
	private String title;

	@Lob
	@Column(nullable = false)
	private String contentMarkdown;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "typeCode")
	@Builder.Default
	private Map<PostGenFile.TypeCode, PostGenFile> images = new HashMap<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PostTag> postTags = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Likes> likes = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Scrap> scraps = new ArrayList<>();

	public void addImage(PostGenFile image) {
		image.setPost(this);
		this.images.put(image.getTypeCode(), image);
	}

	public void addPostTag(PostTag postTag) {
		postTag.setPost(this);
		this.postTags.add(postTag);
	}

	public static Post from(String title, String contentMarkdown, User user) {
		return Post.builder()
			.title(title)
			.contentMarkdown(contentMarkdown)
			.user(user)
			.build();
	}

	public void updateContent(String newTitle, String newContent) {
		this.title = newTitle;
		this.contentMarkdown = newContent;
	}
}