package com.userenrollment.enrollmentservice.service;

import com.userenrollment.enrollmentservice.request.EnrollmentRequest;
import com.userenrollment.enrollmentservice.event.UserEnrolledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public EnrollmentService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enrollUser(EnrollmentRequest request) {
        log.info("Saving enrollment to database... (simulated)");
        
        UserEnrolledEvent event = new UserEnrolledEvent(
                UUID.randomUUID().toString(),
                request.userId(),
                request.courseId(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserEnrolledEvent for user {} in course {}", event.userId(), event.courseId());
    }
}