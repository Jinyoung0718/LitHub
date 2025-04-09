package com.sjy.LitHub.post.mapper;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.post.entity.Likes;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.entity.Scrap;

@Component
public class ToggleMapper {

	public Likes toLikes(Long userId, Long postId) {
		return Likes.builder()
			.user(User.builder().id(userId).build())
			.post(Post.builder().id(postId).build())
			.build();
	}

	public Scrap toScrap(Long userId, Post post) {
		return Scrap.builder()
			.user(User.builder().id(userId).build())
			.post(post)
			.build();
	}
}