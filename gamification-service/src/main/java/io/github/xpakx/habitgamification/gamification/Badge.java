package io.github.xpakx.habitgamification.gamification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Badge {
    BRONZE("Bronze"),
    SILVER("Silver"),
    GOLD("Gold");

    private final String name;
}
