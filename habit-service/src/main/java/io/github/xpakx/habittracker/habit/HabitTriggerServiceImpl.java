package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.TriggerUpdateRequest;
import io.github.xpakx.habittracker.habit.error.NoSuchObjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HabitTriggerServiceImpl implements HabitTriggerService {
    private final HabitTriggerRepository triggerRepository;

    @Override
    public HabitTrigger updateTrigger(Long habitId, TriggerUpdateRequest request, Long userId) {
        HabitTrigger trigger = triggerRepository.findByHabitIdAndHabitUserId(habitId, userId).orElseThrow(() -> new NoSuchObjectException("Trigger for habit with id "+habitId+" not found!"));
        trigger.setName(request.getName());
        return triggerRepository.save(trigger);
    }
}
