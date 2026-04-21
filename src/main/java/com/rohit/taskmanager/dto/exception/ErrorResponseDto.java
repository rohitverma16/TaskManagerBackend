package com.rohit.taskmanager.dto.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto (LocalDateTime localDateTime, int status, String error, String message, String path){
}
