package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/context")
@RequiredArgsConstructor
public class HabitContextController {
    public final HabitContextService service;

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

    @GetMapping("/{contextId}/habit/date")
    public ResponseEntity<List<HabitDetails>> getHabitsForDayAndContext(@RequestParam("date") LocalDateTime date, @PathVariable Long contextId) {
        return new ResponseEntity<>(
                service.getHabitsForDayAndContext(date, contextId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{contextId}/habit/daily")
    public ResponseEntity<List<HabitDetails>> getDailyHabitsForContext(@PathVariable Long contextId) {
        return new ResponseEntity<>(
                service.getHabitsForDayAndContext(LocalDateTime.now(), contextId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{contextId}/habit")
    public ResponseEntity<List<Habit>> getHabitsForContext(@PathVariable Long contextId) {
        return new ResponseEntity<>(
                service.getHabitsForContext(contextId),
                HttpStatus.OK
        );
    }
}
