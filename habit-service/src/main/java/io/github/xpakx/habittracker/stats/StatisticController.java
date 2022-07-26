package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.stats.dto.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticsService service;

    @GetMapping("/context/{contextId}/stats")
    public ResponseEntity<StatsResponse> getStatsForContext(@PathVariable Long contextId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getStatsForContext(contextId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/habit/stats")
    public ResponseEntity<StatsResponse> getOverallStats(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getStats(Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/habit/{habitId}/stats")
    public ResponseEntity<StatsResponse> getStatsForHabit(@PathVariable Long habitId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getStatsForHabit(habitId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
