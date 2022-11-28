package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.discovery.dto.DiscoveryResponse;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.discovery.dto.NamingIslandResponse;
import io.github.xpakx.habitgame.expedition.ResultType;
import io.github.xpakx.habitgame.expedition.error.ExpeditionCompletedException;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.ExpeditionResultRepository;
import io.github.xpakx.habitgame.expedition.ExpeditionService;
import io.github.xpakx.habitgame.expedition.error.ExpeditionNotFoundException;
import io.github.xpakx.habitgame.expedition.error.WrongExpeditionResultType;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {
    private final ExpeditionService expeditionService;
    private final IslandRepository islandRepository;
    private final ExpeditionResultRepository resultRepository;

    @Override
    @Transactional
    public DiscoveryResponse revealIsland(Long expeditionId, Long userId) {
        ExpeditionResult result = resultRepository.findByExpeditionIdAndExpeditionUserId(expeditionId, userId).orElseThrow(ExpeditionNotFoundException::new);
        testResult(result);
        Island island = new Island();
        island.setUserId(userId);
        island.setName("Unnamed");
        islandRepository.save(island);
        expeditionService.completeResult(expeditionId);
        return toDiscoveryResponse(island);
    }

    private DiscoveryResponse toDiscoveryResponse(Island island) {
        DiscoveryResponse response = new DiscoveryResponse();
        response.setIslandId(island.getId());
        return response;
    }

    private void testResult(ExpeditionResult result) {
        if(result.isCompleted()) {
            throw new ExpeditionCompletedException();
        }
        if(result.getType() != ResultType.ISLAND) {
            throw new WrongExpeditionResultType("This expedition did not discover an island!");
        }
    }

    @Override
    public NamingIslandResponse nameIsland(NamingIslandRequest request, Long expeditionId, Long userId) {
        return null;
    }
}
