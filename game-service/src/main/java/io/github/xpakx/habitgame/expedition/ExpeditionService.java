package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ExpeditionEvent;

import java.util.List;

public interface ExpeditionService {
    void addExpedition(ExpeditionEvent event);
    List<Expedition> getActiveExpeditions(Long userId);
}
