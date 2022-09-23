package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.stats.dto.Day;

import java.util.List;

public interface StatisticsService {
    List<Day> getStats(Long userId);
    List<Day> getStatsForContext(Long contextId, Long userId);
    List<Day> getStatsForHabit(Long habitId, Long userId);
}
