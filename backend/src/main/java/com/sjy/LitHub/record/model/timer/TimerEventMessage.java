package com.sjy.LitHub.record.model.timer;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerEventMessage {

	private Long roomId;
	private TimerEventType type;
	private String relatedTime;
	private Long ownerId;

	public static TimerEventMessage of(Long roomId, TimerEventType type, Long ownerId) {
		return TimerEventMessage.builder()
			.roomId(roomId)
			.type(type)
			.relatedTime(LocalDateTime.now().toString())
			.ownerId(ownerId)
			.build();
	}
}