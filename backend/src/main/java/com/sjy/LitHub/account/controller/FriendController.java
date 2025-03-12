package com.sjy.LitHub.account.controller;

import com.sjy.LitHub.account.model.res.FriendListResponseDTO;
import com.sjy.LitHub.account.model.res.FriendRequestResponseDTO;
import com.sjy.LitHub.account.service.friend.FriendService;
import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@SecurityRequirement(name = "accessToken")
@Tag(name = "친구 관리", description = "친구 요청, 수락, 삭제 API")
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @Operation(summary = "친구 요청 보내기", description = "다른 사용자에게 친구 요청을 보냅니다.")
    @PostMapping("/{receiverId}/request")
    public BaseResponse<Empty> sendFriendRequest(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long receiverId) {
        friendService.sendFriendRequest(userPrincipal.getUserId(), receiverId);
        return BaseResponse.success();
    }

    // 친구 요청 수락
    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
    @PatchMapping("/{requestId}/accept")
    public BaseResponse<Empty> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return BaseResponse.success();
    }

    // 친구 요청 거절 (물리 삭제)
    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
    @DeleteMapping("/{requestId}/reject")
    public BaseResponse<Empty> rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return BaseResponse.success();
    }

    // 친구 삭제
    @Operation(summary = "친구 삭제", description = "등록된 친구를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public BaseResponse<Empty> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(friendId);
        return BaseResponse.success();
    }

    // 친구 목록 조회
    @Operation(summary = "내 친구 목록 조회", description = "현재 로그인한 사용자의 친구 목록을 조회합니다.")
    @GetMapping
    public BaseResponse<List<FriendListResponseDTO>> getFriendList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<FriendListResponseDTO> friendList = friendService.getFriendList(userPrincipal.getUserId());
        return BaseResponse.success(friendList);
    }

    // 친구 요청 목록 조회
    @Operation(summary = "받은 친구 요청 목록 조회", description = "현재 로그인한 사용자가 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/requests")
    public BaseResponse<List<FriendRequestResponseDTO>> getPendingFriendRequests(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<FriendRequestResponseDTO> requests = friendService.getPendingFriendRequests(userPrincipal.getUserId());
        return BaseResponse.success(requests);
    }
}