package com.rohit.taskmanager.dto.user;

import com.rohit.taskmanager.entity.Role;

public record UserResponseDto(Long id, String username, Role role) {
}
