package com.sjy.LitHub.account.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.Friend;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.FriendStatus;

@Component
public class FriendMapper {

	public Friend toEntity(Long requesterId, Long receiverId) {
		return Friend.builder()
			.requester(User.builder().id(requesterId).build())
			.receiver(User.builder().id(receiverId).build())
			.status(FriendStatus.PENDING)
			.createdAt(LocalDateTime.now())
			.build();
	}
}