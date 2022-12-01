package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;
import io.github.xpakx.habitgame.discovery.dto.TreasureResponse;

public interface DiscoveryService {
    DiscoveryResponse revealIsland(Long expeditionId, Long userId);
    TreasureResponse getTreasure(Long expeditionId, Long userId);
}
