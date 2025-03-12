package com.sjy.LitHub.account.entity.authenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Tier {
    BRONZE(0, 1000),
    SILVER(1000, 5000),
    GOLD(5000, 10000),
    PLATINUM(10000, Integer.MAX_VALUE);

    private final int minPoints;
    private final int maxPoints;

    public static Tier getTierByPoints(int points) {
        return Arrays.stream(Tier.values())
                .filter(tier -> points >= tier.minPoints && points < tier.maxPoints)
                .findFirst()
                .orElse(BRONZE);
    }
}