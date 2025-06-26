package com.userenrollment.userservice.service;

import com.userenrollment.userservice.model.User;
import com.userenrollment.userservice.repository.UserRepository;
import com.userenrollment.userservice.request.UserRequest;
import com.userenrollment.userservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    public UserService(RabbitTemplate rabbitTemplate, UserRepository userRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.userRepository = userRepository;
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

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserRegisteredEvent for user: {}", event.username());
    }
}
