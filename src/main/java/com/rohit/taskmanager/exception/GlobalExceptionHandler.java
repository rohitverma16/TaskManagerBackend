package com.rohit.taskmanager.exception;

import com.rohit.taskmanager.dto.exception.ErrorResponseDto;
import com.rohit.taskmanager.dto.exception.FieldErrorDto;
import com.rohit.taskmanager.mapper.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private Mapper mapper = new Mapper();

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex, HttpServletRequest request){
        ErrorResponseDto errorResponse=new ErrorResponseDto(
                LocalDateTime.now(),
                400,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                null);

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){

        List<FieldErrorDto> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(mapper::toFieldErrorDto)
                .toList();

        ErrorResponseDto errorResponse=new ErrorResponseDto(
                LocalDateTime.now(),
                400,
                "Bad Request",
                "Validation Failed",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request){
        ErrorResponseDto errorResponse=new ErrorResponseDto(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Something went wrong",
                request.getRequestURI(),
                null);
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
