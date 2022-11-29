package io.github.xpakx.habitgame.island;

import io.github.xpakx.habitgame.island.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.island.dto.NamingIslandResponse;
import io.github.xpakx.habitgame.island.error.IslandNotFoundException;
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
        Island island = islandRepository.findByIdAndUserId(islandId, userId).orElseThrow(IslandNotFoundException::new);
        island.setName(request.getName());
        islandRepository.save(island);
        return toNamingResponse(request.getName(), islandId);
    }

    private NamingIslandResponse toNamingResponse(String name, Long islandId) {
        NamingIslandResponse response = new NamingIslandResponse();
        response.setIslandId(islandId);
        response.setIslandName(name);
        return response;
    }
}
