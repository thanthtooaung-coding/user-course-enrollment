package com.userenrollment.userservice.service;

import com.userenrollment.userservice.model.User;
import com.userenrollment.userservice.repository.UserRepository;
import com.userenrollment.userservice.request.UserRequest;
import com.userenrollment.userservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final String USER_REGISTERED_TOPIC = "user-registered-topic";

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(RedisTemplate<String, String> redisTemplate, UserRepository userRepository, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public void registerUser(UserRequest request) {
        final User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());

        final User savedUser = userRepository.save(user);
        log.info("Saved user to database with ID: {}", savedUser.getId());

        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId().toString(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                Instant.now()
        );

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(USER_REGISTERED_TOPIC, eventJson);
            log.info("Published UserRegisteredEvent to Redis topic '{}'", USER_REGISTERED_TOPIC);
        } catch (Exception e) {
            log.error("Error publishing UserRegisteredEvent to Redis", e);
        }
    }
}
