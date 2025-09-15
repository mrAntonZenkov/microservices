package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getRole()))
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return new UserDto(user.getId(), user.getEmail(), user.getRole());
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        User saved = userService.create(user);
        return new UserDto(saved.getId(), saved.getEmail(), saved.getRole());
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody User user) {
        User saved = userService.update(id, user);
        return new UserDto(saved.getId(), saved.getEmail(), saved.getRole());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
