package com.sjy.LitHub.account.service.UserInfo;

import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.account.mapper.MyPageMapper;
import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.PasswordUpdateRequestDTO;
import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.record.model.MonthlyReadingStatsResponseDTO;
import com.sjy.LitHub.record.model.ReadingRecordResponseDTO;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.service.ReadLogService;
import com.sjy.LitHub.record.service.ReadingStatsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("MyPageService 단위 테스트")
class MyPageServiceTest {

    @Mock
    private MyPageCacheManager myPageCacheManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadLogService readLogService;

    @Mock
    private ReadingStatsService readingStatsService;

    @Mock
    private PasswordManager passwordManager;

    @InjectMocks
    private MyPageService myPageService;

    private UserProfileResponseDTO mockUserProfile;
    private ReadingStatsResponseDTO mockReadingStats;
    private MyPageResponseDTO mockMyPageResponse;

    @BeforeEach
    void setUp() {
        mockUserProfile = new UserProfileResponseDTO(
                "test@example.com",
                "testUser",
                "example.com/image.jpg",
                Tier.BRONZE,
                1000
        );

        mockReadingStats = ReadingStatsResponseDTO.builder()
                .readingStreak(30)
                .readingRecords(List.of(new ReadingRecordResponseDTO(LocalDate.now(), 3)))
                .monthlyStats(List.of(new MonthlyReadingStatsResponseDTO(2024, 3, 100, 10.0, true)))
                .build();

        mockMyPageResponse = MyPageMapper.toMyPageResponse(mockUserProfile, mockReadingStats);
    }

    @Test
    @DisplayName("캐시된 마이페이지 데이터 가져오기 - 캐시 HIT")
    void getCachedMyPageData_CacheHit() {
        Long userId = 1L;

        when(myPageCacheManager.getCachedMyPageData(eq(userId), any())).thenReturn(mockMyPageResponse);

        MyPageResponseDTO result = myPageService.getCachedMyPageData(userId);

        Assertions.assertEquals(mockMyPageResponse, result);
        verify(myPageCacheManager, times(1)).getCachedMyPageData(eq(userId), any());
    }

    @Test
    @DisplayName("캐시되지 않은 마이페이지 데이터 가져오기 - 캐시 MISS")
    void getCachedMyPageData_CacheMiss() {
        Long userId = 1L;

        when(myPageCacheManager.getCachedMyPageData(eq(userId), any())).thenAnswer(invocation -> {
            Supplier<MyPageResponseDTO> fetchFunction = invocation.getArgument(1);
            return fetchFunction.get();
        });

        when(userRepository.getUserProfile(userId)).thenReturn(mockUserProfile);
        when(readingStatsService.getReadingStats(userId)).thenReturn(mockReadingStats);

        MyPageResponseDTO result = myPageService.getCachedMyPageData(userId);

        Assertions.assertEquals(mockMyPageResponse, result);
        verify(myPageCacheManager, times(1)).getCachedMyPageData(eq(userId), any());
        verify(userRepository, times(1)).getUserProfile(userId);
        verify(readingStatsService, times(1)).getReadingStats(userId);
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNickName_Success() {
        Long userId = 1L;
        NicknameRequestDTO requestDto = new NicknameRequestDTO("newNickName");

        when(userRepository.updateNickNameIfNotExists(userId, requestDto.getNickName())).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> myPageService.updateNickName(userId, requestDto));
        verify(userRepository, times(1)).updateNickNameIfNotExists(userId, requestDto.getNickName());
        verify(myPageCacheManager, times(1)).evictCache(userId);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 중복 닉네임")
    void updateNickName_Failure_Duplicate() {
        Long userId = 1L;
        NicknameRequestDTO requestDto = new NicknameRequestDTO("duplicateNickName");

        when(userRepository.updateNickNameIfNotExists(userId, requestDto.getNickName())).thenReturn(false);

        assertThrows(InvalidUserException.class, () -> myPageService.updateNickName(userId, requestDto));

        verify(userRepository, times(1)).updateNickNameIfNotExists(userId, requestDto.getNickName());
        verify(myPageCacheManager, never()).evictCache(anyLong());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_Success() {
        Long userId = 1L;
        PasswordUpdateRequestDTO requestDto = new PasswordUpdateRequestDTO("oldPassword", "newPassword");

        doNothing().when(passwordManager).validatePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());
        doNothing().when(passwordManager).updatePassword(userId, requestDto.getNewPassword());

        myPageService.updatePassword(userId, requestDto);

        verify(passwordManager, times(1)).validatePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());
        verify(passwordManager, times(1)).updatePassword(userId, requestDto.getNewPassword());
        verify(myPageCacheManager, times(1)).evictCache(userId);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_Success() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteUserById(eq(userId), any());

        myPageService.deleteUser(userId);

        verify(userRepository, times(1)).deleteUserById(eq(userId), any());
        verify(myPageCacheManager, times(1)).evictCache(userId);
    }

    @Test
    @DisplayName("독서 기록 저장 후 캐시 업데이트")
    void saveReadingRecordAndUpdateCache_Success() {
        Long userId = 1L;
        int minutes = 30;

        doNothing().when(readLogService).saveReadingRecord(userId, minutes);
        when(myPageCacheManager.getCachedMyPageData(eq(userId), any())).thenReturn(mockMyPageResponse);

        MyPageResponseDTO result = myPageService.saveReadingRecordAndUpdateCache(userId, minutes);

        Assertions.assertEquals(mockMyPageResponse, result);
        verify(readLogService, times(1)).saveReadingRecord(userId, minutes);
        verify(myPageCacheManager, times(1)).evictCache(userId);
        verify(myPageCacheManager, times(1)).getCachedMyPageData(eq(userId), any());
    }
}