package io.github.xpakx.habittracker.clients.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletionEvent {
    Long completionId;
    Long habitId;
    Long userId;
    Integer difficulty;
}
