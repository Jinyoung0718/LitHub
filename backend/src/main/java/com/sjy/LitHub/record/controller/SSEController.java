package com.sjy.LitHub.record.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sjy.LitHub.global.redis.RedisConstants;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.record.service.sse.SSEEmitterService;
import com.sjy.LitHub.record.service.timer.util.TimerConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "SSE", description = "알림 및 스터디룸 타이머용 SSE API (스티키 세션 필요)")
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
public class SSEController {

	private final SSEEmitterService sseEmitterService;

	@Operation(summary = "SSE 연결 - 스터디룸용", description = "스터디룸 타이머 이벤트 수신용 SSE 연결입니다.")
	@GetMapping("/rooms/{roomId}/connect")
	public SseEmitter connectRoom(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId,
		@RequestHeader(value = TimerConstants.HEADER_LAST_EVENT_ID, required = false) String lastEventId
	) {
		String streamKey = RedisConstants.TIMER_EVENT_STREAM;
		return sseEmitterService.connectRoom(roomId, user.getUserId(), lastEventId, streamKey);
	}
}