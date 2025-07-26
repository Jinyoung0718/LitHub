package com.sjy.LitHub.global.init;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.UserInfo.MyPageCacheManager;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.record.entity.ReadLog;
import com.sjy.LitHub.record.entity.ReadLogStats;
import com.sjy.LitHub.record.model.ReadingStatsResponseDTO;
import com.sjy.LitHub.record.repository.readLog.ReadLogRepository;
import com.sjy.LitHub.record.repository.readLogStatus.ReadLogStatsRepository;
import com.sjy.LitHub.record.service.ReadLogStatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class InitialData {

    private final UserRepository userRepository;
    private final UserGenFileMapper userGenFileMapper;
    private final MyPageCacheManager myPageCacheManager;
    private final ReadLogStatusService readLogStatusService;
    private final ReadLogRepository readLogRepository;
    private final ReadLogStatsRepository readLogStatsRepository;

    private static final String PROFILE_KEY_PREFIX = "userProfile:";
    private static final String STATS_KEY_PREFIX = "readingStats:";

    public InitialData(UserRepository userRepository,
        UserGenFileMapper userGenFileMapper,
        MyPageCacheManager myPageCacheManager,
        ReadLogStatusService readLogStatusService,
        ReadLogRepository readLogRepository,
        ReadLogStatsRepository readLogStatsRepository) {
        this.userRepository = userRepository;
        this.userGenFileMapper = userGenFileMapper;
        this.myPageCacheManager = myPageCacheManager;
        this.readLogStatusService = readLogStatusService;
        this.readLogRepository = readLogRepository;
        this.readLogStatsRepository = readLogStatsRepository;
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            String email = "wo0982@naver.com";
            Optional<User> existing = userRepository.findByUserEmailAll(email);

            if (existing.isPresent()) {
                log.info("[InitialData] 이미 초기 데이터 유저가 존재합니다. 초기화 스킵.");
                return;
            }

            // --- 이하 초기 데이터 삽입 로직 ---
            User user = User.builder()
                .nickName("initDataNickName")
                .userEmail(email)
                .password(new BCryptPasswordEncoder().encode("korean12@"))
                .tier(Tier.GOLD)
                .point(6000)
                .role(Role.ROLE_USER)
                .build();

            user = userRepository.save(user);
            Map<UserGenFile.TypeCode, UserGenFile> defaultFiles = userGenFileMapper.toDefaultUserGenFiles(user);
            defaultFiles.values().forEach(user::addUserGenFile);

            user = userRepository.save(user);

            Long userId = user.getId();
            int currentYear = LocalDate.now().getYear();
            Random random = new Random();

            for (int month = 1; month <= 12; month++) {
                int totalTime = random.nextInt(300) + 60;
                int count = random.nextInt(10) + 1;
                double avgTime = totalTime / (double) count;

                ReadLogStats stats = ReadLogStats.builder()
                    .user(user)
                    .year(currentYear)
                    .month(month)
                    .totalReadingTime(totalTime)
                    .readingCount(count)
                    .averageReadingTime(avgTime)
                    .build();
                readLogStatsRepository.save(stats);
            }

            for (int i = 0; i < 10; i++) {
                int day = random.nextInt(28) + 1;
                int readingTime = random.nextInt(60) + 10;
                int colorLevel = random.nextInt(4) + 1;

                LocalDate date = LocalDate.of(currentYear, LocalDate.now().getMonth(), day);

                ReadLog log = ReadLog.builder()
                    .user(user)
                    .date(date)
                    .readingTime(readingTime)
                    .streak(0)
                    .colorLevel(colorLevel)
                    .build();

                readLogRepository.save(log);
            }

            UserProfileResponseDTO profile = userRepository.getUserProfile(userId);
            myPageCacheManager.putCache(PROFILE_KEY_PREFIX + userId, profile);

            ReadingStatsResponseDTO stats = readLogStatusService.getReadingStats(userId, currentYear);
            myPageCacheManager.putCache(STATS_KEY_PREFIX + userId + ":" + currentYear, stats);
        };
    }
}