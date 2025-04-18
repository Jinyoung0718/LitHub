package com.sjy.LitHub.file.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjy.LitHub.file.entity.UserGenFile;

public interface UserGenFileRepository extends JpaRepository<UserGenFile, Long>, UserGenFileRepositoryCustom {

	Optional<UserGenFile> findByUserIdAndTypeCode(Long userId, UserGenFile.TypeCode typeCode);

	default Optional<UserGenFile> findProfile256ByUserId(Long userId) {
		return findByUserIdAndTypeCode(userId, UserGenFile.TypeCode.PROFILE_256);
	}

	default Optional<UserGenFile> findProfile512ByUserId(Long userId) {
		return findByUserIdAndTypeCode(userId, UserGenFile.TypeCode.PROFILE_512);
	}
}