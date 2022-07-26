package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HabitCompletionServiceImpl implements HabitCompletionService {
    private final HabitCompletionRepository completionRepository;
    private final HabitRepository habitRepository;

    @Override
    public HabitCompletion completeHabit(Long habitId, DayRequest request) {
        HabitCompletion completion = new HabitCompletion();
        completion.setDate(request.getDate());
        completion.setHabit(habitRepository.getReferenceById(habitId));
        return completionRepository.save(completion);
    }
}
