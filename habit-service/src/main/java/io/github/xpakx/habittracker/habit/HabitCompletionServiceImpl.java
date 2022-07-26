package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.clients.GamificationPublisher;
import io.github.xpakx.habittracker.habit.dto.CompletionRequest;
import io.github.xpakx.habittracker.habit.error.NoSuchObjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HabitCompletionServiceImpl implements HabitCompletionService {
    private final HabitCompletionRepository completionRepository;
    private final HabitRepository habitRepository;
    private final GamificationPublisher gamificationPublisher;


    @Override
    @Transactional
    public HabitCompletion completeHabit(Long habitId, CompletionRequest request, Long userId) {
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new NoSuchObjectException("Habit with id "+habitId+" not found!"));
        HabitCompletion completion = new HabitCompletion();
        completion.setHabit(habit);
        completion.setDate(request.getDate());
        completion.setUserId(userId);
        completion = completionRepository.save(completion);
        habit.setCompletions(habit.getCompletions() != null ? habit.getCompletions() + 1 : 1);
        if(completedForDay(habitId, habit, request)) {
            habit.setNextDue(habit.getNextDue().plusDays(habit.getInterval()));
            habit.setCompletions(0);
        }
        habitRepository.save(habit);
        gamificationPublisher.sendCompletion(completion);
        return completion;
    }

    private boolean completedForDay(Long habitId, Habit habit, CompletionRequest request) {
        LocalDateTime start = request.getDate().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);
        if(habit.getNextDue().isBefore(start)) {
            return false;
        }
        long count = completionRepository.countByDateBetweenAndHabitId(start, end, habitId);
        return count >= habit.getDailyCompletions();
    }
}
