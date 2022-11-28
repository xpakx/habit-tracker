package io.github.xpakx.habitgame.island;

import io.github.xpakx.habitgame.island.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.island.dto.NamingIslandResponse;

import java.util.List;

public interface IslandService {
    List<Island> getAllIslands(Long userId);
    NamingIslandResponse nameIsland(NamingIslandRequest request, Long islandId, Long userId);
}
