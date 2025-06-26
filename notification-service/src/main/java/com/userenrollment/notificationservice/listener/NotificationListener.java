package com.userenrollment.notificationservice.listener;

import com.userenrollment.notificationservice.config.RabbitMQConfig;
import com.userenrollment.notificationservice.event.CourseCreatedEvent;
import com.userenrollment.notificationservice.event.UserEnrolledEvent;
import com.userenrollment.notificationservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = RabbitMQConfig.USERS_QUEUE_NAME)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("==> Received UserRegisteredEvent: {}", event);
        log.info("Sending 'Welcome Email' to user {} with email {}", event.username(), event.email());
        log.info("Notification processed successfully for User ID: {}", event.userId());
    }

    @RabbitListener(queues = RabbitMQConfig.COURSES_QUEUE_NAME)
    public void handleCourseCreatedEvent(CourseCreatedEvent event) {
        log.info("==> Received CourseCreatedEvent: {}", event);
        log.info("Processing notification for new course '{}' by instructor {}", event.title(), event.instructor());
        log.info("Notification processed successfully for Course ID: {}", event.courseId());
    }

    @RabbitListener(queues = RabbitMQConfig.ENROLLMENTS_QUEUE_NAME)
    public void handleUserEnrolledEvent(UserEnrolledEvent event) {
        log.info("==> Received UserEnrolledEvent: {}", event);
        log.info("Sending 'Enrollment Confirmation' for course {} to user {}", event.courseId(), event.userId());
        log.info("Notification processed successfully for Enrollment ID: {}", event.enrollmentId());
    }
}