package com.sjy.LitHub.record.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.service.UserInfo.MyPageService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.record.service.timer.TimerHeartbeatService;
import com.sjy.LitHub.record.service.timer.TimerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Timer", description = "하트비트 기반 스터디룸 타이머 API")
@RestController
@RequestMapping("/api/timer")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
public class TimerController {

	private final TimerService timerService;
	private final TimerHeartbeatService timerHeartbeatService;
	private final MyPageService myPageService;

	@Operation(summary = "개인 타이머 종료 후 기록 저장", description = "싱글 타이머 종료 후 독서 기록을 저장합니다.")
	@PostMapping("/single")
	public BaseResponse<MyPageResponseDTO> savePersonalReadingRecord(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam int minutes) {

		MyPageResponseDTO myPageResponseDto =
			myPageService.savePersonalReadingSession(userPrincipal.getUserId(), minutes);

		return BaseResponse.success(myPageResponseDto);
	}

	@Operation(summary = "스터디룸 타이머 시작", description = "대표 사용자가 타이머를 시작하면 같은 방 사용자들에게 공유됩니다.")
	@PostMapping("/room/{roomId}/start")
	public BaseResponse<Empty> startGroupTimer(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		timerService.start(roomId, user.getUserId());
		return BaseResponse.success();
	} // 방장만 가능

	@Operation(summary = "스터디룸 타이머 일시정지", description = "대표 사용자만 타이머를 일시정지할 수 있습니다.")
	@PostMapping("/room/{roomId}/pause")
	public BaseResponse<Empty> pauseGroupTimer(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		timerService.pause(roomId, user.getUserId());
		return BaseResponse.success();
	} // 방장만 가능

	@Operation(summary = "스터디룸 타이머 재시작", description = "대표 사용자만 타이머를 재시작할 수 있습니다.")
	@PostMapping("/room/{roomId}/resume")
	public BaseResponse<Empty> resumeGroupTimer(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		timerService.resume(roomId, user.getUserId());
		return BaseResponse.success();
	} // 방장만 가능

	@Operation(summary = "스터디룸 타이머 종료", description = "대표 사용자만 타이머를 종료할 수 있습니다.")
	@PostMapping("/room/{roomId}/stop")
	public BaseResponse<Empty> stopGroupTimer(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		timerService.stop(roomId, user.getUserId());
		return BaseResponse.success();
	} // 방장만 가능

	@Operation(
		summary = "스터디룸 하트비트",
		description = " 방장만 유효하며, 클라이언트는 약 10초 간격으로 호출하세요. 서버는 하트비트를 TTL 30초로 관리하며, 방장의 하트비트가 끊기면 타이머가 만료됩니다."
	)
	@PostMapping("/room/{roomId}/heartbeat")
	public BaseResponse<Empty> heartbeat(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		timerHeartbeatService.receiveHeartbeat(roomId, user.getUserId());
		return BaseResponse.success();
	} // 방장만 가능
}