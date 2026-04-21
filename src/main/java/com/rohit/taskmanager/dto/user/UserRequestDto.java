package com.rohit.taskmanager.dto.user;

import com.rohit.taskmanager.entity.Role;

public record UserRequestDto(String username, String password, Role role) {
    public UserRequestDto {
        if (role == null) {
            role = Role.USER; // default role
        }
    }
}
