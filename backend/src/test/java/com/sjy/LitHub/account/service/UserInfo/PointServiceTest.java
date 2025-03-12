package com.sjy.LitHub.account.service.UserInfo;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.account.repository.point.PointRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("PointService 단위 테스트")
class PointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void setUp() {
        User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password123")
                .profileImageUrlSmall("example.com/small.jpg")
                .profileImageUrlLarge("example.com/large.jpg")
                .tier(Tier.BRONZE)
                .point(1000)
                .role(Role.ROLE_USER)
                .deletedAt(null)
                .build();
    }

    @Test
    @DisplayName("사용자 포인트 및 티어 업데이트 - 성공")
    void updateUserPointsAndTier_Success() {
        Long userId = 1L;
        int minutes = 30;

        // given
        when(userRepository.existsById(userId)).thenReturn(true);

        // when
        assertDoesNotThrow(() -> pointService.updateUserPointsAndTier(userId, minutes));

        // then
        verify(userRepository, times(1)).existsById(userId);
        verify(pointRepository, times(1)).updateUserPointsAndTier(userId, minutes);
    }

    @Test
    @DisplayName("사용자 포인트 및 티어 업데이트 - 실패 (존재하지 않는 사용자)")
    void updateUserPointsAndTier_Failure_UserNotFound() {
        Long userId = 1L;
        int minutes = 30;

        // given
        when(userRepository.existsById(userId)).thenReturn(false);

        // when & then
        assertThrows(InvalidUserException.class, () -> pointService.updateUserPointsAndTier(userId, minutes));
        verify(userRepository, times(1)).existsById(userId);
        verify(pointRepository, never()).updateUserPointsAndTier(anyLong(), anyInt());
    }
}
