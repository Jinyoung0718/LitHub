package com.sjy.LitHub.global.init;

import java.util.Map;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;

@Configuration
public class InitialData {

    private final UserRepository userRepository;
    private final UserGenFileMapper userGenFileMapper;

    public InitialData(UserRepository userRepository, UserGenFileMapper userGenFileMapper) {
        this.userRepository = userRepository;
        this.userGenFileMapper = userGenFileMapper;
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            User user = User.builder()
                .nickName("initDataNickName")
                .userEmail("wo0982@naver.com")
                .password(new BCryptPasswordEncoder().encode("korean12@"))
                .role(Role.ROLE_USER)
                .build();

            Map<UserGenFile.TypeCode, UserGenFile> defaultFiles = userGenFileMapper.toDefaultUserGenFiles(user);
            defaultFiles.values().forEach(user::addUserGenFile);
            userRepository.save(user);
        };
    }
}