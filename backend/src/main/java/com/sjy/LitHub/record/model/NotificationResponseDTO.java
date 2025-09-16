package com.sjy.LitHub.record.model;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.record.entity.StudyGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

	private String groupName;
	private String senderName;
	private Long groupId;

	public NotificationResponseDTO(StudyGroup group, User sender) {
		this.groupName = group.getTitle();
		this.senderName = sender.getNickName();
		this.groupId = group.getId();
	}
}