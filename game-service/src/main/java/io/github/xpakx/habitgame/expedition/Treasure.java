package io.github.xpakx.habitgame.expedition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Treasure {
    SMALL("Small"),
    MEDIUM("Medium"),
    BIG("Big"),
    RARE("Rare"),
    LEGENDARY("Legendary");

    private final String name;
}
