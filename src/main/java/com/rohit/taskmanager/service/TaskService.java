package com.rohit.taskmanager.service;

import com.rohit.taskmanager.dto.task.TaskRequestDto;
import com.rohit.taskmanager.dto.task.TaskResponseDto;
import com.rohit.taskmanager.entity.Status;
import com.rohit.taskmanager.entity.Task;
import com.rohit.taskmanager.entity.User;
import com.rohit.taskmanager.mapper.Mapper;
import com.rohit.taskmanager.repo.TaskRepo;
import com.rohit.taskmanager.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Mapper mapper;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequest) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        Task task = mapper.toSaveTask(taskRequest);
        task.setUser(user);
        taskRepo.save(task);
        return mapper.toTaskResponse(task);
    }


    public Page<TaskResponseDto> getTaskByStatus(Status status, int page, int pageSize) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        return taskRepo.findByUserAndStatus(user, status, PageRequest.of(page, pageSize))
                .map(mapper::toTaskResponse);
    }

    public Page<TaskResponseDto> getTaskByUser(int page, int pageSize) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        return taskRepo.findByUser(user, PageRequest.of(page, pageSize))
                .map(mapper::toTaskResponse);
    }

    public TaskResponseDto updateTask(Long id, Status status) {
        Task task = taskRepo.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
        if(status.equals(Status.COMPLETED)){
            task.setCompletedAt(LocalDateTime.now());
        }
        task.setStatus(status);
        taskRepo.save(task);
        return mapper.toTaskResponse(task);
    }

    public void deleteTaskByUserId(Long id) {
        userRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        taskRepo.deleteByUserId(id);
    }

    private String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
