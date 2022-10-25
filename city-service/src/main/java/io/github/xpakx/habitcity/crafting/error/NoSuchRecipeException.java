package io.github.xpakx.habitcity.crafting.error;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoSuchRecipeException extends RuntimeException {
    public NoSuchRecipeException(String message) {
        super(message);
    }
    public NoSuchRecipeException() {
        super("No such recipe!");
    }
}