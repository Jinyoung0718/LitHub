package com.sjy.LitHub.account.util;

import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordManager {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordValidation passwordValidation;

    public void validatePassword(Long userId, String currentPassword, String newPassword) {
        String currentEncodedPassword = userRepository.findPasswordById(userId);
        if (currentEncodedPassword == null) {
            throw new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(currentPassword, currentEncodedPassword)) {
            throw new InvalidUserException(BaseResponseStatus.INVALID_PASSWORD);
        }

        if (isInvalid(newPassword)) {
            throw new InvalidUserException(BaseResponseStatus.USER_PASSWORD_INVALID);
        }
    }

    public boolean isInvalid(String password) {
        return !passwordValidation.isValid(password);
    }

    public void updatePassword(Long userId, String newPassword) {
        userRepository.updatePasswordById(userId, passwordEncoder.encode(newPassword));
    }
}