package com.sjy.LitHub.account.repository.point;

public interface PointRepositoryCustom {
    void updateUserPointsAndTier(Long userId, int minutes);
}