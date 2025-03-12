package com.sjy.LitHub.global.init;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.repository.user.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class InitialData {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitialData(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            User user = User.builder()
                    .nickName("initDataNickName")
                    .userEmail("wo0982@naver.com")
                    .password(passwordEncoder.encode("korean12@"))
                    .profileImageUrlSmall("http://localhost:8080/Image/baseprofile/base-profile_48.webp")
                    .profileImageUrlLarge("http://localhost:8080/Image/baseprofile/base-profile_128.webp")
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user);
        };
    }
}