package com.userenrollment.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userenrollment.notificationservice.event.CourseCreatedEvent;
import com.userenrollment.notificationservice.event.UserEnrolledEvent;
import com.userenrollment.notificationservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisMessageSubscriber.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String data = new String(message.getBody());
        log.info("Received message from Redis topic '{}'", channel);

        try {
            if (channel.equals("user-registered-topic")) {
                UserRegisteredEvent event = objectMapper.readValue(data, UserRegisteredEvent.class);
                log.info("==> Parsed UserRegisteredEvent: {}", event);
                log.info("Sending 'Welcome Email' to user {}", event.username());
            } else if (channel.equals("course-created-topic")) {
                CourseCreatedEvent event = objectMapper.readValue(data, CourseCreatedEvent.class);
                log.info("==> Parsed CourseCreatedEvent: {}", event);
                log.info("Processing notification for new course '{}'", event.title());
            } else if (channel.equals("user-enrolled-topic")) {
                UserEnrolledEvent event = objectMapper.readValue(data, UserEnrolledEvent.class);
                log.info("==> Parsed UserEnrolledEvent: {}", event);
                log.info("Sending 'Enrollment Confirmation' for user {}", event.userId());
            }
        } catch (Exception e) {
            log.error("Error parsing message from Redis", e);
        }
    }
}