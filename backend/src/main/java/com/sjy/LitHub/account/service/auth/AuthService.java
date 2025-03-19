package com.sjy.LitHub.account.service.auth;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.mapper.UserMapper;
import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.redis.RedisService;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordManager passwordManager;
    private final RedisService redisService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String REDIS_KEY_PREFIX = "emailAuth:";
    private static final String REDIS_KEY_SUFFIX = ":verified";

    @Transactional
    public void signup(SignupDTO signupDTO) {
        SignupValidationStatus.validateAll(signupDTO, userRepository, redisService, passwordManager);
        createUser(signupDTO);
    }

    private void createUser(SignupDTO signupDTO) {
        User user = userMapper.ofSignupDTO(signupDTO);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
        redisService.deleteData(REDIS_KEY_PREFIX + signupDTO.getUserEmail() + REDIS_KEY_SUFFIX);
    }

    @Transactional
    public void restoreUser(String email) {
        userRepository.restoreUserByEmail(email);
    }

    @Transactional(readOnly = true)
    public void validateNicknameAvailability(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new InvalidUserException(BaseResponseStatus.USER_NICKNAME_DUPLICATE);
        }
    }
}