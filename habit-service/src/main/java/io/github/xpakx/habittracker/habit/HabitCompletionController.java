package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.CompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HabitCompletionController {
    private final HabitCompletionService service;

    @PostMapping("/habit/{habitId}/completion")
    public ResponseEntity<HabitCompletion> completeHabit(@RequestBody CompletionRequest request, @PathVariable Long habitId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.completeHabit(habitId, request, Long.valueOf(id)),
                HttpStatus.CREATED
        );
    }
}
