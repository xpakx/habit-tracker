package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandResponse;

public interface DiscoveryService {
    DiscoveryResponse revealIsland(Long expeditionId, Long userId);
    NamingIslandResponse nameIsland(NamingIslandRequest request, Long expeditionId, Long userId);
}
