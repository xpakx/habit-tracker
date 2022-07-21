package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/context")
@RequiredArgsConstructor
public class HabitContextController {
    public final HabitContextService service;
    public final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitContext> addContext(@RequestBody HabitContextRequest request) {
        return new ResponseEntity<>(
                service.addContext(request),
                HttpStatus.OK
        );
    }
    @PutMapping("/{contextId}")
    public ResponseEntity<HabitContext> updateContext(@RequestBody HabitContextRequest request, @PathVariable Long contextId) {
        return new ResponseEntity<>(
                service.updateContext(contextId, request),
                HttpStatus.OK
        );
    }


    @GetMapping("/{contextId}/habit")
    public ResponseEntity<List<Habit>> getHabitsForDayAndContext(@RequestBody DayRequest request, @PathVariable Long contextId) {
        return new ResponseEntity<>(
                habitService.getHabitsForDayAndContext(request, contextId),
                HttpStatus.OK
        );
    }
}
