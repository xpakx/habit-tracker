package io.github.xpakx.habitgame.island;

import io.github.xpakx.habitgame.island.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.island.dto.NamingIslandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IslandServiceImpl implements IslandService {
    private final IslandRepository islandRepository;

    @Override
    public List<Island> getAllIslands(Long userId) {
        return islandRepository.findByUserId(userId);
    }

    @Override
    public NamingIslandResponse nameIsland(NamingIslandRequest request, Long islandId, Long userId) {
        return null;
    }
}
