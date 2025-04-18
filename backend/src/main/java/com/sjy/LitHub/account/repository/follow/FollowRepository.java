package com.sjy.LitHub.account.repository.follow;

import com.sjy.LitHub.account.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {
}