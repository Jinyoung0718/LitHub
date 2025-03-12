package com.sjy.LitHub.account.repository.user;

import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;

import java.time.LocalDateTime;

public interface UserRepositoryCustom {

    UserProfileResponseDTO getUserProfile(Long userId);

    boolean updateNickNameIfNotExists(Long userId, String newNickName);

    void deleteUserById(Long userId, LocalDateTime deletedAt);
}