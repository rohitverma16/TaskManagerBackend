package com.rohit.taskmanager.dto.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto (
        LocalDateTime localDateTime,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDto> validationErrors){
}
