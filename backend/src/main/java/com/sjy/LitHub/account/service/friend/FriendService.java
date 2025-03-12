package com.sjy.LitHub.account.service.friend;

import com.sjy.LitHub.account.model.res.FriendListResponseDTO;
import com.sjy.LitHub.account.model.res.FriendRequestResponseDTO;
import com.sjy.LitHub.account.repository.friend.FriendRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    // 친구 요청 보내기
    @Transactional
    public void sendFriendRequest(Long requesterId, Long receiverId) {
        if (friendRepository.insertIfNotExists(requesterId, receiverId) == 0) {
            throw new InvalidUserException(BaseResponseStatus.FRIEND_REQUEST_ALREADY_SENT);
        }
    }

    // 친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        if (friendRepository.updateFriendStatusToAccepted(requestId) == 0) {
            throw new InvalidUserException(BaseResponseStatus.FRIEND_REQUEST_NOT_FOUND);
        }
    }

    // 친구 요청 거절 (물리 삭제)
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        if (friendRepository.deleteFriendRequest(requestId) == 0) {
            throw new InvalidUserException(BaseResponseStatus.FRIEND_REQUEST_NOT_FOUND);
        }
    }

    // 친구 삭제
    @Transactional
    public void deleteFriend(Long friendId) {
        if (friendRepository.deleteFriend(friendId) == 0) {
            throw new InvalidUserException(BaseResponseStatus.FRIEND_NOT_FOUND);
        }
    }

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FriendListResponseDTO> getFriendList(Long userId) {
        return friendRepository.findAcceptedFriendsByUserId(userId);
    }

    // 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<FriendRequestResponseDTO> getPendingFriendRequests(Long userId) {
        return friendRepository.findPendingFriendRequests(userId);
    }
}