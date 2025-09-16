package com.sjy.LitHub.record.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.record.model.NotificationResponseDTO;
import com.sjy.LitHub.record.service.group.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "알림 관련 API")
@RestController
@RequestMapping("/api/notification")
@SecurityRequirement(name = "accessToken")
@RequiredArgsConstructor
@Validated
public class NotificationController {

	private final NotificationService notificationService;

	@Operation(summary = "초대 알림 목록 조회", description = "현재 로그인한 사용자의 스터디 그룹 초대 알림을 조회합니다.")
	@GetMapping("/invites")
	public BaseResponse<List<NotificationResponseDTO>> getInviteNotifications(
		@AuthenticationPrincipal UserPrincipal user
	) {
		List<NotificationResponseDTO> notificationResponseDTOS = notificationService.getInviteNotifications(user.getUserId());
		return BaseResponse.success(notificationResponseDTOS);
	}

	@Operation(summary = "초대 알림 삭제", description = "roomId에 대한 초대 알림을 삭제합니다. 보통 사용자가 초대를 거절하거나 처리 완료 시 사용합니다.")
	@DeleteMapping("/invites/{roomId}")
	public BaseResponse<Empty> clearInvite(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		notificationService.clearInvite(roomId, user.getUserId());
		return BaseResponse.success();
	}
}