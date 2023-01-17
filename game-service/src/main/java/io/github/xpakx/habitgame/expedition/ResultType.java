package io.github.xpakx.habitgame.expedition;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultType {
    NONE("None"),
    TREASURE("Treasure"),
    BATTLE("Battle"),
    ISLAND("Island");

    private final String name;
}
