package com.rohit.taskmanager.controller;

import com.rohit.taskmanager.dto.task.TaskRequestDto;
import com.rohit.taskmanager.dto.task.TaskResponseDto;
import com.rohit.taskmanager.entity.Status;
import com.rohit.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto taskRequestDto){
        TaskResponseDto task = taskService.createTask(taskRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TaskResponseDto> getTasks(@RequestParam(required = false) Status status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Page<TaskResponseDto> tasks;
        if(status != null){
            tasks=taskService.getTaskByStatus(status, page, size);
        }
        else{
            tasks=taskService.getTaskByUser(page, size);
        }
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(@PathVariable Long id, @RequestParam Status status){
        TaskResponseDto taskResponseDto = taskService.updateTask(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDto);
    }
}
