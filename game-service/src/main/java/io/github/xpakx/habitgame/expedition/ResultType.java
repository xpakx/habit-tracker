package io.github.xpakx.habitgame.expedition;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultType {
    TREASURE("Treasure"),
    BATTLE("Battle"),
    MONSTER("Monster");

    private final String name;
}
