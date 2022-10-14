package io.github.xpakx.habitcity.error.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String status;
    private Integer error;
}
