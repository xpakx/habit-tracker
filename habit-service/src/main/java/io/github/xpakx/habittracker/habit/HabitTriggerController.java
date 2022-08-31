package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.TriggerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HabitTriggerController {
    private final HabitTriggerService service;

    @PutMapping("/habit/{habitId}/trigger")
    public ResponseEntity<HabitTrigger> updateTrigger(@RequestBody TriggerUpdateRequest request, @PathVariable Long habitId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.updateTrigger(habitId, request, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
