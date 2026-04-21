package com.rohit.taskmanager.dto.task;

import com.rohit.taskmanager.entity.Status;

import java.time.LocalDateTime;

public record TaskResponseDto(String title, String description, Status status, LocalDateTime createdAt, LocalDateTime completedAt) {
}
