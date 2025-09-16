package com.sjy.LitHub.record.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.record.model.group.GroupCreateRequestDTO;
import com.sjy.LitHub.record.model.group.GroupJoinRequestDTO;
import com.sjy.LitHub.record.service.group.InviteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Invite_Group", description = "스터디 그룹 관련 API")
@RestController
@RequestMapping("/api/group")
@SecurityRequirement(name = "accessToken")
@RequiredArgsConstructor
public class GroupController {

	private final InviteService inviteService;

	@Operation(summary = "스터디 그룹 생성", description = "스터디 그룹을 새로 생성합니다.")
	@PostMapping("/create")
	public BaseResponse<Long> createGroup(@RequestBody @Valid GroupCreateRequestDTO request) {
		Long groupId = inviteService.createGroup(request);
		return BaseResponse.success(groupId);
	}

	@Operation(summary = "스터디 그룹 초대", description = "roomId에 유저를 초대합니다.")
	@PostMapping("/invite")
	public BaseResponse<Empty> inviteUserToRoom(
		@AuthenticationPrincipal UserPrincipal user,
		@RequestBody @Valid GroupJoinRequestDTO request
	) {
		inviteService.inviteUserToRoom(request.getRoomId(), user.getUserId(), request.getTargetUserId());
		return BaseResponse.success();
	}

	@Operation(
		summary = "스터디 그룹 참가",
		description = "초대받은 사용자가 해당 스터디 그룹에 참가합니다. 참가 후에는 SSE 연결을 통해 실시간 방 상태 및 타이머 이벤트를 수신할 수 있습니다."
	)
	@PostMapping("/{roomId}/join")
	public BaseResponse<Empty> joinRoom(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		inviteService.joinRoom(roomId, user.getUserId());
		return BaseResponse.success();
	}

	@Operation(summary = "스터디 그룹 퇴장", description = "사용자가 현재 참여 중인 스터디 그룹에서 퇴장합니다.")
	@PostMapping("/{roomId}/exit")
	public BaseResponse<Empty> exitGroup(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable Long roomId
	) {
		inviteService.exitRoom(roomId, user.getUserId());
		return BaseResponse.success();
	}
}