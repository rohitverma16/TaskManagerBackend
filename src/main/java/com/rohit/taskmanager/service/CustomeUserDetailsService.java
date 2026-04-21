package com.rohit.taskmanager.service;

import com.rohit.taskmanager.dto.user.UserRequestDto;
import com.rohit.taskmanager.dto.user.UserResponseDto;
import com.rohit.taskmanager.entity.User;
import com.rohit.taskmanager.mapper.Mapper;
import com.rohit.taskmanager.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class CustomeUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Mapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));
    }

    @Transactional
    public void save(UserRequestDto userRequestDto) {
        if(userRepo.findByUsername(userRequestDto.username()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User saveUser = mapper.toSaveUser(userRequestDto);
        userRepo.save(saveUser);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));
    }


    public List<UserResponseDto> findAll() {
        return userRepo.findAll()
                .stream()
                .map(mapper::toUserResponseDto)
                .toList();
    }

    public void deleteUser(Long id) {
        userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exists"));
        userRepo.deleteById(id);
    }

}
