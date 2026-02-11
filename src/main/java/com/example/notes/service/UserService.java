package com.example.notes.service;

import com.example.notes.dto.RegisterRequest;
import com.example.notes.dto.UserResponse;
import com.example.notes.exception.ResourceNotFoundException;
import com.example.notes.exception.UserAlreadyExistsException;
import com.example.notes.model.User;
import com.example.notes.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("Username already exists!");
        }
    }

    public void validateEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User Email is not unique!");
        }
    }

    public UserResponse registerUser(RegisterRequest request) {
        validateUsername(request.getUsername());
        validateEmail(request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public UserResponse loginUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found!"));

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            throw new ResourceNotFoundException("Incorrect password!");
        }
        return mapToResponse(user);
    }

    public UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }

}