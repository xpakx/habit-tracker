package io.github.xpakx.habitcity.equipment.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EquipmentFullException extends RuntimeException {
    public EquipmentFullException(String message) {
        super(message);
    }
    public EquipmentFullException() {
        super("Not enough space in equipment!");
    }
}