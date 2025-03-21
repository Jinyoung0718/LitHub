package com.sjy.LitHub.account.repository.user.custom;

import com.sjy.LitHub.account.model.res.UserProfileResponseDTO;

public interface UserRepositoryCustom {

    UserProfileResponseDTO getUserProfile(Long userId);

    boolean updateNickNameIfNotExists(Long userId, String newNickName);
}