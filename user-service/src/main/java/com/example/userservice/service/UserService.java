package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User userUpdated) {
        User user = getById(id);
        user.setEmail(userUpdated.getEmail());
        user.setPassword(userUpdated.getPassword());
        user.setRole(userUpdated.getRole());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
