package com.rohit.taskmanager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDto(
        @NotBlank(message = "Title can not be blank")
        @Size(min = 4, max = 50, message = "Title must be between 4 and 50 characters")
        String title,
        @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
        String description,

        String status) {
}
