package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HabitContextServiceImpl implements HabitContextService {
    private final HabitContextRepository contextRepository;

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
}
