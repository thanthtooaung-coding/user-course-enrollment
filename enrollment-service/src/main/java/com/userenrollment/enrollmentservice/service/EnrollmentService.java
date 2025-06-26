package com.userenrollment.enrollmentservice.service;

import com.userenrollment.enrollmentservice.model.Enrollment;
import com.userenrollment.enrollmentservice.repository.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(RabbitTemplate rabbitTemplate, EnrollmentRepository enrollmentRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void enrollUser(EnrollmentRequest request) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(UUID.fromString(request.userId()));
        enrollment.setCourseId(UUID.fromString(request.courseId()));
        enrollment.setEnrollmentDate(Instant.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Saved enrollment to database with ID: {}", savedEnrollment.getId());

        UserEnrolledEvent event = new UserEnrolledEvent(
                savedEnrollment.getId().toString(),
                savedEnrollment.getUserId().toString(),
                savedEnrollment.getCourseId().toString(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserEnrolledEvent for user {} in course {}", event.userId(), event.courseId());
    }
}