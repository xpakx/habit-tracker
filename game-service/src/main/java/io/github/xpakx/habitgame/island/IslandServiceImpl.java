package io.github.xpakx.habitgame.island;

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
}
