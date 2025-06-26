package com.userenrollment.courseservice.service;

import com.userenrollment.courseservice.request.CourseRequest;
import com.userenrollment.courseservice.event.CourseCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public CourseService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createCourse(CourseRequest request) {
        log.info("Saving course to database... (simulated)");
        
        CourseCreatedEvent event = new CourseCreatedEvent(
                UUID.randomUUID().toString(),
                request.title(),
                request.instructor(),
                request.price(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published CourseCreatedEvent for course: {}", event.title());
    }
}