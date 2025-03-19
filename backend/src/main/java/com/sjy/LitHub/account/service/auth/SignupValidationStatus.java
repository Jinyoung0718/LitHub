package com.sjy.LitHub.account.service.auth;

import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.redis.RedisService;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

enum SignupValidationStatus {

    EMAIL_NOT_VERIFIED {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            String emailVerified = redisService.getData("emailAuth:" + signupDto.getUserEmail() + ":verified");
            if (!"true".equals(emailVerified)) {
                throw new InvalidUserException(BaseResponseStatus.EMAIL_VERIFICATION_REQUIRED);
            }
        }
    },

    PASSWORD_INVALID {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            if (passwordManager.isInvalid(signupDto.getUserPassword())) {
                throw new InvalidUserException(BaseResponseStatus.USER_PASSWORD_INVALID);
            }
        }
    },

    NICKNAME_DUPLICATE {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            if (userRepository.existsByNickName(signupDto.getNickName())) {
                throw new InvalidUserException(BaseResponseStatus.USER_NICKNAME_DUPLICATE);
            }
        }
    },

    USER_ALREADY_EXISTS {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            if (userRepository.findByUserEmailAll(signupDto.getUserEmail()).isPresent()) {
                throw new InvalidUserException(BaseResponseStatus.USER_ALREADY_EXISTS);
            }
        }
    },

    USER_RECOVERY_REQUIRED {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            userRepository.findByUserEmailAll(signupDto.getUserEmail())
                    .ifPresent(user -> {
                        if (user.getDeletedAt() != null) {
                            throw new InvalidUserException(BaseResponseStatus.USER_RECOVERY_REQUIRED);
                        }
                    });
        }
    },

    OAUTH_USER_EXISTS {
        @Override
        public void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
            if (userRepository.findByUserEmailAll(signupDto.getUserEmail()).isPresent()) {
                throw new InvalidUserException(BaseResponseStatus.USER_ALREADY_EXISTS);
            }
        }
    };

    abstract void validate(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager);

    public static void validateAll(SignupDTO signupDto, UserRepository userRepository, RedisService redisService, PasswordManager passwordManager) {
        for (SignupValidationStatus status : SignupValidationStatus.values()) {
            status.validate(signupDto, userRepository, redisService, passwordManager);
        }
    }
}