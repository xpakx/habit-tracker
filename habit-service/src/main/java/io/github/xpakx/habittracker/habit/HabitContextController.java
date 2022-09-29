package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.ContextDetails;
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
    public ResponseEntity<HabitContext> addContext(@RequestBody HabitContextRequest request, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.addContext(request, Long.valueOf(id)),
                HttpStatus.CREATED
        );
    }
    @PutMapping("/{contextId}")
    public ResponseEntity<HabitContext> updateContext(@RequestBody HabitContextRequest request, @PathVariable Long contextId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.updateContext(contextId, request, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{contextId}/habit/date")
    public ResponseEntity<List<HabitDetails>> getHabitsForDayAndContext(@RequestParam("date") LocalDateTime date, @PathVariable Long contextId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getHabitsForDayAndContext(date, contextId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{contextId}/habit/daily")
    public ResponseEntity<List<HabitDetails>> getDailyHabitsForContext(@PathVariable Long contextId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getHabitsForDayAndContext(LocalDateTime.now(), contextId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{contextId}/habit")
    public ResponseEntity<List<Habit>> getHabitsForContext(@PathVariable Long contextId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getHabitsForContext(contextId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContextDetails>> getAllContexts(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getAllContexts(Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
