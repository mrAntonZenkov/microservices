package com.example.userservice.service;

import com.example.userservice.dto.UserRequestDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.PasswordHash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User create(UserRequestDto dto) throws Exception {
        User user = userMapper.toEntity(dto);
        user.setPassword(PasswordHash.hash(dto.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequestDto dto) throws Exception{
        User user = getById(id);
        if (dto.getEmail() != null){
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null){
            user.setPassword(PasswordHash.hash(dto.getPassword()));
        }
        if (dto.getRole() != null){
            user.setRole(dto.getRole());
        }
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
