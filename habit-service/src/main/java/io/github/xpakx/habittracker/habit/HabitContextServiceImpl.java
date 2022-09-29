package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitDetails;
import io.github.xpakx.habittracker.habit.error.NoSuchObjectException;
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
    public HabitContext addContext(HabitContextRequest request, Long userId) {
        HabitContext context = new HabitContext();
        context.setName(request.getName());
        context.setDescription(request.getDescription());
        context.setTimeBounded(request.isTimeBounded());
        context.setActiveStart(request.getActiveStart());
        context.setActiveEnd(request.getActiveEnd());
        context.setUserId(userId);
        return contextRepository.save(context);
    }

    @Override
    public HabitContext updateContext(Long contextId, HabitContextRequest request, Long userId) {
        HabitContext context = contextRepository.findByIdAndUserId(contextId, userId).orElseThrow(() -> new NoSuchObjectException("No context with id "+ contextId+"!"));
        context.setName(request.getName());
        context.setDescription(request.getDescription());
        context.setTimeBounded(request.isTimeBounded());
        context.setActiveStart(request.getActiveStart());
        context.setActiveEnd(request.getActiveEnd());
        return contextRepository.save(context);
    }

    @Override
    public List<HabitDetails> getHabitsForDayAndContext(LocalDateTime request, Long contextId, Long userId) {
        LocalDateTime start = request.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);
        return habitRepository.findByNextDueBetweenAndContextIdAndUserId(start, end, contextId, userId);
    }

    @Override
    public List<Habit> getHabitsForContext(Long contextId, Long userId) {
        return habitRepository.findByContextIdAndUserId(contextId, userId);
    }

    @Override
    public List<ContextDetails> getAllContexts(Long userId) {
        return contextRepository.findProjectedByUserId(userId);
    }
}
