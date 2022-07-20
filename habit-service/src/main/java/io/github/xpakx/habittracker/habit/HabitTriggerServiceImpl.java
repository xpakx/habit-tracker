package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.TriggerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HabitTriggerServiceImpl implements HabitTriggerService {
    private final HabitTriggerRepository triggerRepository;

    @Override
    public HabitTrigger updateTrigger(Long habitId, TriggerUpdateRequest request) {
        HabitTrigger trigger = triggerRepository.findByHabitId(habitId).orElseThrow();
        trigger.setName(request.getName());
        return triggerRepository.save(trigger);
    }
}
