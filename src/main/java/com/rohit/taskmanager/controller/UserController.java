package com.rohit.taskmanager.controller;

import com.rohit.taskmanager.dto.user.UserResponseDto;
import com.rohit.taskmanager.entity.User;
import com.rohit.taskmanager.repo.TaskRepo;
import com.rohit.taskmanager.service.CustomeUserDetailsService;
import com.rohit.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    CustomeUserDetailsService userDetailsService;
    @Autowired
    TaskService taskService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userDetailsService.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsers(@PathVariable Long id) {
        taskService.deleteTaskByUserId(id);
        userDetailsService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted");
    }
}
