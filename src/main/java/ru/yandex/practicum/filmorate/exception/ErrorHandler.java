package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(final ResourceNotFoundException e) {
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handCloneException(final RuntimeException e) {
        return new ErrorResponse("внутренняя ошибка сервера", e.getMessage());
    }

    public ErrorResponse handOthers(final RuntimeException e) {
        return new ErrorResponse("Внутренняя ошибка сервера", e.getMessage());
    }
}
