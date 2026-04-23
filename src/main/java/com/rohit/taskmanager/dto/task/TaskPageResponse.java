package com.rohit.taskmanager.dto.task;

import java.util.List;

public record TaskPageResponse(
        List<TaskResponseDto> taskResponseDtos,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}