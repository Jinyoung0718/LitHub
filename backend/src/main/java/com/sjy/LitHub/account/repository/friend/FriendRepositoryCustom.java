package com.sjy.LitHub.account.repository.friend;

import com.sjy.LitHub.account.model.res.FriendListResponseDTO;
import com.sjy.LitHub.account.model.res.FriendRequestResponseDTO;

import java.util.List;

public interface FriendRepositoryCustom {

    long insertIfNotExists(Long requesterId, Long receiverId);

    List<FriendListResponseDTO> findAcceptedFriendsByUserId(Long userId);

    List<FriendRequestResponseDTO> findPendingFriendRequests(Long userId);

    List<Long> findFriendIdsByUserId(Long userId);
}