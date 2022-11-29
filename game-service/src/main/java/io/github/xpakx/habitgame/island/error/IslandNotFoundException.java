package io.github.xpakx.habitgame.island.error;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IslandNotFoundException extends RuntimeException  {
    public IslandNotFoundException(String message) {
        super(message);
    }
    public IslandNotFoundException() {
        super("Island not found!");
    }
}