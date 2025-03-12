package com.sjy.LitHub.account.service.UserInfo;

import com.sjy.LitHub.account.repository.point.PointRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateUserPointsAndTier(Long userId, int minutes) {
        if (!userRepository.existsById(userId)) {
            throw new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND);
        }
        pointRepository.updateUserPointsAndTier(userId, minutes);
    }
}