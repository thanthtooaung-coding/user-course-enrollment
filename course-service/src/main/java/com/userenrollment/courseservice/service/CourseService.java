package com.userenrollment.courseservice.service;

import com.userenrollment.courseservice.event.CourseCreatedEvent;
import com.userenrollment.courseservice.exception.CourseNotFoundException;
import com.userenrollment.courseservice.model.Course;
import com.userenrollment.courseservice.repository.CourseRepository;
import com.userenrollment.courseservice.request.CourseRequest;
import com.userenrollment.courseservice.response.CourseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // CREATE
    public CourseResponse createCourse(CourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setInstructor(request.instructor());
        course.setPrice(request.price());

        Course savedCourse = courseRepository.save(course);
        log.info("Saved course to database with ID: {}", savedCourse.getId());

        publishCourseCreatedEvent(savedCourse);
        return CourseResponse.fromCourse(savedCourse);
    }

    // READ (All)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseResponse::fromCourse)
                .collect(Collectors.toList());
    }

    // READ (By ID)
    public CourseResponse getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return CourseResponse.fromCourse(course);
    }

    // UPDATE
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        existingCourse.setTitle(request.title());
        existingCourse.setInstructor(request.instructor());
        existingCourse.setPrice(request.price());

        Course updatedCourse = courseRepository.save(existingCourse);
        return CourseResponse.fromCourse(updatedCourse);
    }

    // DELETE
    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
        log.info("Deleted course with ID: {}", id);
    }

    private void publishCourseCreatedEvent(Course course) {
        CourseCreatedEvent event = new CourseCreatedEvent(
                course.getId().toString(),
                course.getTitle(),
                course.getInstructor(),
                course.getPrice(),
                Instant.now()
        );
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        log.info("Published CourseCreatedEvent for course: {}", event.title());
    }
}