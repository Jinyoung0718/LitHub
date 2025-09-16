package com.sjy.LitHub.account.service.UserInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.PasswordUpdateRequestDTO;
import com.sjy.LitHub.account.model.res.MyPageResponseDTO;
import com.sjy.LitHub.account.model.res.StudyGroupHistoryDTO;
import com.sjy.LitHub.account.model.res.StudyGroupHistoryListDTO;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.account.util.UserCacheKeyConstants;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.repository.group.StudyGroupRepository;
import com.sjy.LitHub.record.service.logs.ReadLogService;
import com.sjy.LitHub.record.service.logs.ReadLogStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageCacheManager myPageCacheManager;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final PasswordManager passwordManager;
    private final ReadLogStatusService readLogStatusService;
    private final ReadLogService readLogService;
    private final TransactionAfterCommitExecutor afterCommitExecutor;

    @Transactional(readOnly = true)
    public MyPageResponseDTO getCachedMyPageData(Long userId, int year) {

        UserProfileResponseDTO userProfile = myPageCacheManager.getOrFetchAndPut(
            UserCacheKeyConstants.profileKey(userId),
            () -> userRepository.getUserProfile(userId),
            UserProfileResponseDTO.class
        );

        ReadingStatsResponseDTO readingStats = myPageCacheManager.getOrFetchAndPut(
            UserCacheKeyConstants.statsKey(userId, year),
            () -> readLogStatusService.getReadingStats(userId, year),
            ReadingStatsResponseDTO.class
        );

        StudyGroupHistoryListDTO studiesWrapper = myPageCacheManager.getOrFetchAndPut(
            UserCacheKeyConstants.studyKey(userId),
            () -> StudyGroupHistoryListDTO.of(
                studyGroupRepository.findRecentEndedWithMembersByUser(userId, 10)
            ),
            StudyGroupHistoryListDTO.class
        );

        return MyPageResponseDTO.of(userProfile, readingStats, studiesWrapper.getItems());
    }

    @Transactional
    public void updateNickName(Long userId, NicknameRequestDTO requestDto) {
        if (!userRepository.updateNickNameIfNotExists(userId, requestDto.getNickName())) {
            throw new InvalidUserException(BaseResponseStatus.USER_NICKNAME_DUPLICATE);
        }
        UserProfileResponseDTO updatedProfile = userRepository.getUserProfile(userId);

        afterCommitExecutor.executeAfterCommit(() ->
            myPageCacheManager.putCache(UserCacheKeyConstants.profileKey(userId), updatedProfile)
        );
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequestDTO requestDto) {
        passwordManager.validatePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());
        passwordManager.updatePassword(userId, requestDto.getNewPassword());

        afterCommitExecutor.executeAfterCommit(() ->
            myPageCacheManager.evictCache(UserCacheKeyConstants.profileKey(userId))
        );
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId, LocalDateTime.now());

        afterCommitExecutor.executeAfterCommit(() -> {
            myPageCacheManager.evictCache(UserCacheKeyConstants.profileKey(userId));
            myPageCacheManager.evictCache(UserCacheKeyConstants.statsKey(userId, LocalDate.now().getYear()));
            myPageCacheManager.evictCache(UserCacheKeyConstants.studyKey(userId));
        });
    }

    @Transactional
    public MyPageResponseDTO savePersonalReadingSession(Long userId, int minutes) {
        readLogService.saveReadingRecord(userId, minutes);
        int year = LocalDate.now().getYear();

        ReadingStatsResponseDTO updatedStats = readLogStatusService.getReadingStats(userId, year);
        UserProfileResponseDTO updatedProfile = userRepository.getUserProfile(userId);

        afterCommitExecutor.executeAfterCommit(() -> {
            myPageCacheManager.putCache(UserCacheKeyConstants.statsKey(userId, year), updatedStats);
            myPageCacheManager.putCache(UserCacheKeyConstants.profileKey(userId), updatedProfile);
        });

        String studiesKey = UserCacheKeyConstants.studyKey(userId);
        List<StudyGroupHistoryDTO> studies = myPageCacheManager
            .getCache(studiesKey, StudyGroupHistoryListDTO.class)
            .map(StudyGroupHistoryListDTO::getItems)
            .orElse(Collections.emptyList());

        return MyPageResponseDTO.of(updatedProfile, updatedStats, studies);
    }

    @Transactional
    public void saveGroupReadingSession(Collection<Long> userIds, int minutes) {
        if (userIds == null || userIds.isEmpty()) return;
        int currentYear = LocalDate.now().getYear();

        for (Long userId : userIds) {
            readLogService.saveReadingRecord(userId, minutes);

            ReadingStatsResponseDTO updatedStats = readLogStatusService.getReadingStats(userId, currentYear);
            UserProfileResponseDTO updatedProfile = userRepository.getUserProfile(userId);

            afterCommitExecutor.executeAfterCommit(() -> {
                myPageCacheManager.putCache(UserCacheKeyConstants.statsKey(userId, currentYear), updatedStats);
                myPageCacheManager.putCache(UserCacheKeyConstants.profileKey(userId), updatedProfile);
                myPageCacheManager.evictCache(UserCacheKeyConstants.studyKey(userId));
            });
        }
    }
}