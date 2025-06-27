package com.userenrollment.userservice.service;


import com.userenrollment.userservice.event.UserRegisteredEvent;
import com.userenrollment.userservice.exception.UserNotFoundException;
import com.userenrollment.userservice.model.User;
import com.userenrollment.userservice.repository.UserRepository;
import com.userenrollment.userservice.request.UserRequest;
import com.userenrollment.userservice.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RabbitTemplate rabbitTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    public UserResponse registerUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        log.info("Saved user to database with ID: {}", savedUser.getId());

        publishUserRegisteredEvent(savedUser);
        return UserResponse.fromUser(savedUser);
    }

    // READ (All)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // READ (By ID)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.fromUser(user);
    }

    // UPDATE
    public UserResponse updateUser(UUID id, UserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        existingUser.setUsername(request.username());
        existingUser.setEmail(request.email());
        if (request.password() != null && !request.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return UserResponse.fromUser(updatedUser);
    }

    // DELETE
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }

    private void publishUserRegisteredEvent(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserRegisteredEvent for user: {}", event.username());
    }
}