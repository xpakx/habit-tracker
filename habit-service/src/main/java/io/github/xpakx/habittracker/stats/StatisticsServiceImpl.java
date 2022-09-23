package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.habit.HabitCompletion;
import io.github.xpakx.habittracker.habit.HabitCompletionRepository;
import io.github.xpakx.habittracker.stats.dto.Day;
import io.github.xpakx.habittracker.stats.dto.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<Integer, List<HabitCompletion>> map =  completions.stream()
                .collect(Collectors.groupingBy((c) -> c.getDate().getDayOfYear()));
        List<Day> heatMapElems = map.keySet().stream()
                .map((c) -> new Day(c, map.get(c).size()))
                .toList();
        StatsResponse response = new StatsResponse();
        response.setDays(heatMapElems);
        response.setCompletions(completions.size());
        return response;
    }
}
