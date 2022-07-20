package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
