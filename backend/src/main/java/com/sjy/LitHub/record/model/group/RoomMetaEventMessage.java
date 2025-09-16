package com.sjy.LitHub.record.model.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMetaEventMessage {
	private Long roomId;
	private Long userId;
	private RoomMetaEventType type;

	public static RoomMetaEventMessage of(Long roomId, Long userId, RoomMetaEventType type) {
		return RoomMetaEventMessage.builder()
			.roomId(roomId)
			.userId(userId)
			.type(type)
			.build();
	}
}