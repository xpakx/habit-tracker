package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;

public interface DiscoveryService {
    DiscoveryResponse revealIsland(Long expeditionId, Long userId);
}
