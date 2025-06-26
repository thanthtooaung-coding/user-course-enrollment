package com.userenrollment.userservice.service;

import com.userenrollment.userservice.request.UserRequest;
import com.userenrollment.userservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public UserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void registerUser(UserRequest request) {
        log.info("Saving user to database... (simulated)");

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                request.username(),
                request.email(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserRegisteredEvent for user: {}", event.username());
    }
}
