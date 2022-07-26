package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitContextServiceImpl implements HabitContextService {
    private final HabitContextRepository contextRepository;
    private final HabitRepository habitRepository;

    @Override
    public HabitContext addContext(HabitContextRequest request) {
        HabitContext context = new HabitContext();
        context.setName(request.getName());
        context.setDescription(request.getDescription());
        context.setTimeBounded(request.isTimeBounded());
        context.setActiveStart(request.getActiveStart());
        context.setActiveEnd(request.getActiveEnd());
        return contextRepository.save(context);
    }

    @Override
    public HabitContext updateContext(Long contextId, HabitContextRequest request) {
        HabitContext context = contextRepository.findById(contextId).orElseThrow();
        context.setName(request.getName());
        context.setDescription(request.getDescription());
        context.setTimeBounded(request.isTimeBounded());
        context.setActiveStart(request.getActiveStart());
        context.setActiveEnd(request.getActiveEnd());
        return contextRepository.save(context);
    }

    @Override
    public List<HabitDetails> getHabitsForDayAndContext(DayRequest request, Long contextId) {
        LocalDateTime start = request.getDate().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);
        return habitRepository.findByNextDueBetweenAndContextId(start, end, contextId);
    }

    @Override
    public List<Habit> getHabitsForContext(Long contextId) {
        return habitRepository.findByContextId(contextId);
    }
}
