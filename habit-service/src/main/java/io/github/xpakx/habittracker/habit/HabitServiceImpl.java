package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final HabitContextRepository contextRepository;

    @Transactional
    public Habit addHabit(HabitRequest request, Long userId) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setInterval(request.getInterval());
        habit.setDailyCompletions(request.getDailyCompletions());
        habit.setStart(request.getStart());
        habit.setNextDue(request.getStart());
        habit.setCompletions(0);
        habit.setUserId(userId);
        habit.setContext(request.getContextId() != null ? contextRepository.getReferenceById(request.getContextId()) : null);
        habit.setDifficulty(request.getDifficulty());
        HabitTrigger trigger = new HabitTrigger();
        trigger.setUserId(userId);
        trigger.setName(request.getTriggerName());
        habit.setTrigger(trigger);
        return habitRepository.save(habit);
    }

    @Override
    public Habit updateHabit(Long habitId, HabitUpdateRequest request, Long userId) {
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId).orElseThrow();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setInterval(request.getInterval());
        habit.setDailyCompletions(request.getDailyCompletions());
        habit.setStart(request.getStart());
        habit.setDifficulty(request.getDifficulty());
        return habitRepository.save(habit);
    }

    @Override
    public List<Habit> getHabitsForDay(LocalDateTime date, Long userId) {
        LocalDateTime start = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);
        return habitRepository.findByNextDueBetweenAndUserId(start, end, userId);
    }

    @Override
    public List<Habit> getAllHabits(Long userId) {
        return habitRepository.findAllByUserId(userId);
    }
}
