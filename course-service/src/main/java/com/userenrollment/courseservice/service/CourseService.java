package com.userenrollment.courseservice.service;

import com.userenrollment.courseservice.model.Course;
import com.userenrollment.courseservice.repository.CourseRepository;
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
    private final CourseRepository courseRepository;

    public CourseService(RabbitTemplate rabbitTemplate, CourseRepository courseRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.courseRepository = courseRepository;
    }

    public void createCourse(CourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setInstructor(request.instructor());
        course.setPrice(request.price());

        Course savedCourse = courseRepository.save(course);
        log.info("Saved course to database with ID: {}", savedCourse.getId());

        CourseCreatedEvent event = new CourseCreatedEvent(
                savedCourse.getId().toString(),
                savedCourse.getTitle(),
                savedCourse.getInstructor(),
                savedCourse.getPrice(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published CourseCreatedEvent for course: {}", event.title());
    }
}