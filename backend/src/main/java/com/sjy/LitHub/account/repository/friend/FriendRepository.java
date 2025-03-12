package com.sjy.LitHub.account.repository.friend;

import com.sjy.LitHub.account.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
}