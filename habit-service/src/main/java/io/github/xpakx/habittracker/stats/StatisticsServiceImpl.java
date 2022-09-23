package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.habit.HabitCompletion;
import io.github.xpakx.habittracker.habit.HabitCompletionRepository;
import io.github.xpakx.habittracker.stats.dto.Day;
import io.github.xpakx.habittracker.stats.dto.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final HabitCompletionRepository completionRepository;

    @Override
    public StatsResponse getStats(Long userId) {
        List<HabitCompletion> completions = completionRepository.findByUserId(userId);
        return completionsToStats(completions);
    }

    @Override
    public StatsResponse getStatsForContext(Long contextId, Long userId) {
        List<HabitCompletion> completions = completionRepository.findByUserIdAndHabitContextId(userId, contextId);
        return completionsToStats(completions);
    }

    @Override
    public StatsResponse getStatsForHabit(Long habitId, Long userId) {
        List<HabitCompletion> completions = completionRepository.findByUserIdAndHabitId(userId, habitId);
        return completionsToStats(completions);
    }

    private StatsResponse completionsToStats(List<HabitCompletion> completions) {
        return null;
    }
}
