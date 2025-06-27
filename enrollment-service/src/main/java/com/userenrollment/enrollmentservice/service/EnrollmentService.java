package com.userenrollment.enrollmentservice.service;

import com.userenrollment.enrollmentservice.event.UserEnrolledEvent;
import com.userenrollment.enrollmentservice.exception.EnrollmentNotFoundException;
import com.userenrollment.enrollmentservice.model.Enrollment;
import com.userenrollment.enrollmentservice.repository.EnrollmentRepository;
import com.userenrollment.enrollmentservice.request.EnrollmentRequest;
import com.userenrollment.enrollmentservice.response.EnrollmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;
    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final EnrollmentRepository enrollmentRepository;
    private final RestTemplate restTemplate;

    public EnrollmentService(RabbitTemplate rabbitTemplate, EnrollmentRepository enrollmentRepository, RestTemplate restTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.enrollmentRepository = enrollmentRepository;
        this.restTemplate = restTemplate;
    }

    // CREATE
    public EnrollmentResponse enrollUser(EnrollmentRequest request) {
        validateExternalId("http://USER-SERVICE/api/users/", request.userId(), "User");
        validateExternalId("http://COURSE-SERVICE/api/courses/", request.courseId(), "Course");

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(UUID.fromString(request.userId()));
        enrollment.setCourseId(UUID.fromString(request.courseId()));
        enrollment.setEnrollmentDate(Instant.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Saved enrollment to database with ID: {}", savedEnrollment.getId());

        publishUserEnrolledEvent(savedEnrollment);
        return EnrollmentResponse.fromEnrollment(savedEnrollment);
    }

    public EnrollmentResponse getEnrollmentById(UUID id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(id));
        return EnrollmentResponse.fromEnrollment(enrollment);
    }

    public List<EnrollmentResponse> getEnrollmentsByUserId(UUID userId) {
        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(EnrollmentResponse::fromEnrollment)
                .collect(Collectors.toList());
    }

    // DELETE
    public void cancelEnrollment(UUID id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EnrollmentNotFoundException(id);
        }
        enrollmentRepository.deleteById(id);
        log.info("Canceled (deleted) enrollment with ID: {}", id);
    }

    private void validateExternalId(String baseUrl, String id, String type) {
        try {
            String url = baseUrl + id;
            restTemplate.headForHeaders(url);
            log.info("{} validation successful for {}Id: {}", type, type.toLowerCase(), id);
        } catch (HttpClientErrorException e) {
            log.error("Validation failed: {} not found with ID: {}", type, id);
            throw new IllegalArgumentException("Invalid " + type.toLowerCase() + "Id: " + id);
        }
    }

    private void publishUserEnrolledEvent(Enrollment enrollment) {
        UserEnrolledEvent event = new UserEnrolledEvent(
                enrollment.getId().toString(),
                enrollment.getUserId().toString(),
                enrollment.getCourseId().toString(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published UserEnrolledEvent for user {} in course {}", event.userId(), event.courseId());
    }
}