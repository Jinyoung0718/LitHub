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

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", indexes = {
	@Index(name = "idx_post_user_id", columnList = "user_id"), // 작성자 기준 검색용
	@Index(name = "idx_post_user_created", columnList = "user_id, created_at") // 특정 사용자의 게시글 시간순 정렬
})
public class Post extends BaseTime {

	@Column(nullable = false, length = 200)
	private String title;

	@Lob
	@Column(nullable = false)
	private String contentMarkdown;

	@Lob
	@Column(nullable = false)
	private String searchContent;

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
		String html = markdownToHtml(contentMarkdown);
		String searchContent = Jsoup.parse(html).text();

		return Post.builder()
			.title(title)
			.contentMarkdown(contentMarkdown)
			.searchContent(searchContent)
			.user(user)
			.build();
	}

	public static String markdownToHtml(String markdown) {
		Parser parser = Parser.builder().build();
		org.commonmark.node.Node document = parser.parse(markdown);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}

	public void updateContent(String newTitle, String newContent) {
		this.title = newTitle;
		this.contentMarkdown = newContent;
	}
}