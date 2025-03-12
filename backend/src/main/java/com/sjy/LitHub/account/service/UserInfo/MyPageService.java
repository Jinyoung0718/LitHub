package com.sjy.LitHub.account.service.UserInfo;

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
import com.sjy.LitHub.record.service.ReadingStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageCacheManager myPageCacheManager;
    private final ReadLogService readLogService;
    private final ReadingStatsService readingStatsService;
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;

    @Transactional(readOnly = true)
    public MyPageResponseDTO getCachedMyPageData(Long userId) {
        return myPageCacheManager.getCachedMyPageData(userId, () -> getMyPageData(userId));
    }

    @Transactional(readOnly = true)
    public MyPageResponseDTO getMyPageData(Long userId) {
        UserProfileResponseDTO userProfile = userRepository.getUserProfile(userId);
        ReadingStatsResponseDTO readingStats = readingStatsService.getReadingStats(userId);
        return MyPageMapper.toMyPageResponse(userProfile, readingStats);
    } // 캐싱되지 않은 경우에만 실행됨

    public void updateNickName(Long userId, NicknameRequestDTO requestDto) {
        if (!userRepository.updateNickNameIfNotExists(userId, requestDto.getNickName())) {
            throw new InvalidUserException(BaseResponseStatus.USER_NICKNAME_DUPLICATE);
        }
        myPageCacheManager.evictCache(userId);
    } // 닉네임을 변경하면 응답 데이터 없이 성공만 반환하고, 프론트에서 다시 전체 데이터를 요청하도록 설계

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequestDTO requestDto) {
        passwordManager.validatePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());
        passwordManager.updatePassword(userId, requestDto.getNewPassword());
        myPageCacheManager.evictCache(userId);
    } // 로그인 화면으로 리다이렉트

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId, LocalDateTime.now());
        myPageCacheManager.evictCache(userId);
    } // 로그인 화면으로 리다이렉트

    @Transactional
    public MyPageResponseDTO saveReadingRecordAndUpdateCache(Long userId, int minutes) {
        readLogService.saveReadingRecord(userId, minutes);
        myPageCacheManager.evictCache(userId);
        return myPageCacheManager.getCachedMyPageData(userId, () -> getMyPageData(userId));
    } // ReadLogService 에서 저장 처리
}