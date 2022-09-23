package io.github.xpakx.habittracker.stats.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatsResponse {
    List<Day> days;
    Integer completions;
}
