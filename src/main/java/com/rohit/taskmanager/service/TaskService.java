package com.rohit.taskmanager.service;

import com.rohit.taskmanager.dto.task.TaskPageResponse;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "tasks",allEntries = true)
    public TaskResponseDto createTask(TaskRequestDto taskRequest) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        Task task = mapper.toSaveTask(taskRequest);
        task.setUser(user);
        taskRepo.save(task);
        return mapper.toTaskResponse(task);
    }



    public TaskPageResponse getTaskByStatus(Status status, int page, int pageSize) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        Page<TaskResponseDto> tasks = taskRepo.findByUserAndStatus(user, status, PageRequest.of(page, pageSize))
                .map(mapper::toTaskResponse);
        return mapper.toTaskPageResponse(tasks);
    }

    @Cacheable(value = "tasks",key = "#root.target.getCurrentUserName() + '_' + #page + '_' + #pageSize")
    public TaskPageResponse getTaskByUser(int page, int pageSize) {
        String username = getCurrentUserName();
        User user = userRepo.findByUsername(username).orElseThrow(()->new RuntimeException("Username not found"));
        Page<TaskResponseDto> tasks = taskRepo.findByUser(user, PageRequest.of(page, pageSize))
                .map(mapper::toTaskResponse);
        return mapper.toTaskPageResponse(tasks);
    }

    @CacheEvict(value = "tasks",allEntries = true)
    public TaskResponseDto updateTask(Long id, Status status) {
        Task task = taskRepo.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
        if(status.equals(Status.COMPLETED)){
            task.setCompletedAt(LocalDateTime.now());
        }
        task.setStatus(status);
        taskRepo.save(task);
        return mapper.toTaskResponse(task);
    }

    @CacheEvict(value = "tasks",allEntries = true)
    public void deleteTaskByUserId(Long id) {
        userRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        taskRepo.deleteByUserId(id);
    }

    public String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
