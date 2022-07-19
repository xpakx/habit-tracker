package io.github.xpakx.habittracker.service;

import io.github.xpakx.habittracker.entity.Habit;
import io.github.xpakx.habittracker.entity.HabitTrigger;
import io.github.xpakx.habittracker.entity.dto.HabitRequest;
import io.github.xpakx.habittracker.repo.HabitContextRepository;
import io.github.xpakx.habittracker.repo.HabitRepository;
import io.github.xpakx.habittracker.repo.HabitTriggerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final HabitContextRepository contextRepository;
    private final HabitTriggerRepository triggerRepository;

    @Transactional
    public Habit addHabit(HabitRequest request) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setInterval(request.getInterval());
        habit.setStart(request.getStart());
        habit.setContext(contextRepository.getReferenceById(request.getContextId()));
        HabitTrigger trigger = new HabitTrigger();
        trigger.setName(request.getTriggerName());
        habit.setTrigger(trigger);
        return habitRepository.save(habit);
    }
}
