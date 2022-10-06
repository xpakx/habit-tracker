package io.github.xpakx.habitgamification.badge;

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
