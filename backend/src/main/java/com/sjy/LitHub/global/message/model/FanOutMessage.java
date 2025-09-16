package com.sjy.LitHub.global.message.model;

import java.time.ZoneId;

import com.sjy.LitHub.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FanOutMessage {
	private Long userId;
	private Long postId;
	private long createdAtMillis;

	public static FanOutMessage from(Post post) {
		long createdAtMillis = post.getCreatedAt()
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();

		return new FanOutMessage(post.getUser().getId(), post.getId(), createdAtMillis);
	}
}