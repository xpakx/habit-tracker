package io.github.xpakx.habittracker.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Day {
    LocalDate date;
    int completions;
}
