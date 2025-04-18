package com.sjy.LitHub.account.service.friend;

import com.sjy.LitHub.account.repository.follow.FriendRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService;

    @Test
    @DisplayName("친구 요청 보내기 - 성공")
    void sendFriendRequest_Success() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        given(friendRepository.insertIfNotExists(requesterId, receiverId)).willReturn(1L);

        assertDoesNotThrow(() -> friendService.sendFriendRequest(requesterId, receiverId));

        verify(friendRepository, times(1)).insertIfNotExists(requesterId, receiverId);
    }

    @Test
    @DisplayName("친구 요청 보내기 - 실패 (이미 요청됨)")
    void sendFriendRequest_Failure_AlreadyExists() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        given(friendRepository.insertIfNotExists(requesterId, receiverId)).willReturn(0L);

        assertThrows(InvalidUserException.class, () -> friendService.sendFriendRequest(requesterId, receiverId));

        verify(friendRepository, times(1)).insertIfNotExists(requesterId, receiverId);
    }

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void acceptFriendRequest_Success() {
        Long requestId = 1L;
        when(friendRepository.updateFriendStatusToAccepted(anyLong())).thenReturn(1L);

        assertDoesNotThrow(() -> friendService.acceptFriendRequest(requestId));

        verify(friendRepository, times(1)).updateFriendStatusToAccepted(requestId);
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (존재하지 않음)")
    void acceptFriendRequest_Failure_NotFound() {
        Long requestId = 1L;

        given(friendRepository.updateFriendStatusToAccepted(requestId)).willReturn(0L);

        assertThrows(InvalidUserException.class, () -> friendService.acceptFriendRequest(requestId));

        verify(friendRepository, times(1)).updateFriendStatusToAccepted(requestId);
    }

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void rejectFriendRequest_Success() {
        Long requestId = 1L;

        given(friendRepository.deleteFriendRequest(requestId)).willReturn(1L);

        assertDoesNotThrow(() -> friendService.rejectFriendRequest(requestId));

        verify(friendRepository, times(1)).deleteFriendRequest(requestId);
    }

    @Test
    @DisplayName("친구 요청 거절 - 실패 (존재하지 않음)")
    void rejectFriendRequest_Failure_NotFound() {
        Long requestId = 1L;

        given(friendRepository.deleteFriendRequest(requestId)).willReturn(0L);

        assertThrows(InvalidUserException.class, () -> friendService.rejectFriendRequest(requestId));

        verify(friendRepository, times(1)).deleteFriendRequest(requestId);
    }

    @Test
    @DisplayName("친구 삭제 - 성공")
    void deleteFriend_Success() {
        Long friendId = 1L;

        given(friendRepository.deleteFriend(friendId)).willReturn(1L);

        assertDoesNotThrow(() -> friendService.deleteFriend(friendId));

        verify(friendRepository, times(1)).deleteFriend(friendId);
    }

    @Test
    @DisplayName("친구 삭제 - 실패 (존재하지 않음)")
    void deleteFriend_Failure_NotFound() {
        Long friendId = 1L;

        given(friendRepository.deleteFriend(friendId)).willReturn(0L);

        assertThrows(InvalidUserException.class, () -> friendService.deleteFriend(friendId));

        verify(friendRepository, times(1)).deleteFriend(friendId);
    }
}