package com.sjy.LitHub.file.repository.user;

import java.util.List;
import java.util.Set;

import com.sjy.LitHub.file.entity.UserGenFile;

public interface UserGenFileRepositoryCustom {
	List<UserGenFile> findProfiles256ByUserIds(Set<Long> userIds);
}