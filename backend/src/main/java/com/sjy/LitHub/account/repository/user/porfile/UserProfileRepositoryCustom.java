package com.sjy.LitHub.account.repository.user.porfile;

import java.util.Optional;

import com.sjy.LitHub.account.entity.User;

public interface UserProfileRepositoryCustom {
	Optional<User> findUserWithGenFilesById(Long userId);
}