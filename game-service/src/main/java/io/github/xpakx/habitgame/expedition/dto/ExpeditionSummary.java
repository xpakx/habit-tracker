package io.github.xpakx.habitgame.expedition.dto;

import io.github.xpakx.habitgame.expedition.ResultType;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ExpeditionSummary {
    Long getId();
    Long getUserId();
    LocalDateTime getStart();
    LocalDateTime getEnd();
    boolean isFinished();
    boolean isReturning();
    LocalDateTime getReturnEnd();

    @Value("#{target.expeditionResult != null ? target.expeditionResult.type : null}")
    ResultType getResult();
}
