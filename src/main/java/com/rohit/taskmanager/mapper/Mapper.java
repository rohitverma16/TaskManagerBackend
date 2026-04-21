package com.rohit.taskmanager.mapper;

import com.rohit.taskmanager.dto.task.TaskRequestDto;
import com.rohit.taskmanager.dto.task.TaskResponseDto;
import com.rohit.taskmanager.dto.user.UserRequestDto;
import com.rohit.taskmanager.dto.user.UserResponseDto;
import com.rohit.taskmanager.entity.Status;
import com.rohit.taskmanager.entity.Task;
import com.rohit.taskmanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Mapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User toSaveUser(UserRequestDto userRequestDto) {
        return new User(
                userRequestDto.username(),
                passwordEncoder.encode(userRequestDto.password()),
                userRequestDto.role());
    }

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRole()
                );
    }

    public Task toSaveTask(TaskRequestDto taskRequestDto){
        return new Task(
                null,
                taskRequestDto.title(),
                taskRequestDto.description(),
                Status.TODO,
                LocalDateTime.now(),
                null);
    }

    public TaskResponseDto toTaskResponse(Task task){
        return new TaskResponseDto(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getCompletedAt());
    }
}
