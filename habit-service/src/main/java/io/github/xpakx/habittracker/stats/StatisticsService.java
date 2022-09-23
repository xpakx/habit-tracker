package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.stats.dto.StatsResponse;


public interface StatisticsService {
    StatsResponse getStats(Long userId);
    StatsResponse getStatsForContext(Long contextId, Long userId);
    StatsResponse getStatsForHabit(Long habitId, Long userId);
}
