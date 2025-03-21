package com.sjy.LitHub.account.service.UserInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.mapper.MyPageMapper;
import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.PasswordUpdateRequestDTO;
import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.service.ReadLogService;
import com.sjy.LitHub.record.service.ReadLogStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageCacheManager myPageCacheManager;
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final ReadLogStatusService readLogStatusService;
    private final ReadLogService readLogService;

    @Transactional(readOnly = true)
    public MyPageResponseDTO getCachedMyPageData(Long userId) {
        UserProfileResponseDTO userProfile = getCachedOrFetch("userProfile:" + userId,
            () -> userRepository.getUserProfile(userId), UserProfileResponseDTO.class);

        ReadingStatsResponseDTO readingStats = getCachedOrFetch("readingStats:" + userId,
            () -> readLogStatusService.getReadingStats(userId, LocalDate.now().getYear()), ReadingStatsResponseDTO.class);

        return MyPageMapper.toMyPageResponse(userProfile, readingStats);
    }

    private <T> T getCachedOrFetch(String key, Supplier<T> fetchData, Class<T> type) {
        return myPageCacheManager.getCache(key, type)
            .orElseGet(() -> {
                T data = fetchData.get();
                myPageCacheManager.putCache(key, data);
                return data;
            });
    }

    @Transactional
    public void updateNickName(Long userId, NicknameRequestDTO requestDto) {
        if (!userRepository.updateNickNameIfNotExists(userId, requestDto.getNickName())) {
            throw new InvalidUserException(BaseResponseStatus.USER_NICKNAME_DUPLICATE);
        }
        UserProfileResponseDTO updatedProfile = userRepository.getUserProfile(userId);
        myPageCacheManager.putCache("userProfile:" + userId, updatedProfile);
    } // 닉네임을 변경하면 응답 데이터 없이 성공만 반환하고, 프론트에서 다시 전체 데이터를 요청하도록 설계

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequestDTO requestDto) {
        passwordManager.validatePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());
        passwordManager.updatePassword(userId, requestDto.getNewPassword());
        myPageCacheManager.evictCache("userProfile:" + userId);
    } // 프론트에서 로그아웃 처리 후 재로그인 유도

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId, LocalDateTime.now());
        myPageCacheManager.evictCache("userProfile:" + userId);
        myPageCacheManager.evictCache("readingStats:" + userId);
    } // 로그인 화면으로 리다이렉트

    @Transactional
    public MyPageResponseDTO saveReadingRecordAndUpdateCache(Long userId, int minutes) {
        int currentYear = LocalDate.now().getYear();
        readLogService.saveReadingRecord(userId, minutes);

        ReadingStatsResponseDTO updatedStats = readLogStatusService.getReadingStats(userId, currentYear);
        myPageCacheManager.putCache("readingStats:" + userId, updatedStats);

        UserProfileResponseDTO updatedProfile = userRepository.getUserProfile(userId);
        myPageCacheManager.putCache("userProfile:" + userId, updatedProfile);
        return MyPageMapper.toMyPageResponse(updatedProfile, updatedStats);
    } // ReadLogService 에서 저장 처리
}