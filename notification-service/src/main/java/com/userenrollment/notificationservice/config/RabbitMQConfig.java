package com.userenrollment.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Configuration for User Events ---
    public static final String USERS_EXCHANGE_NAME = "users-exchange";
    public static final String USERS_QUEUE_NAME = "users-notifications-queue";
    public static final String USERS_ROUTING_KEY = "user.registered";

    @Bean
    public TopicExchange usersExchange() {
        return new TopicExchange(USERS_EXCHANGE_NAME);
    }

    @Bean
    public Queue usersQueue() {
        return new Queue(USERS_QUEUE_NAME, true);
    }

    @Bean
    public Binding usersBinding(@Qualifier("usersQueue") Queue queue, @Qualifier("usersExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(USERS_ROUTING_KEY);
    }

    // --- Configuration for Course Events ---
    public static final String COURSES_EXCHANGE_NAME = "courses-exchange";
    public static final String COURSES_QUEUE_NAME = "courses-notifications-queue";
    public static final String COURSES_ROUTING_KEY = "course.created";

    @Bean
    public TopicExchange coursesExchange() {
        return new TopicExchange(COURSES_EXCHANGE_NAME);
    }

    @Bean
    public Queue coursesQueue() {
        return new Queue(COURSES_QUEUE_NAME, true);
    }

    @Bean
    public Binding coursesBinding(@Qualifier("coursesQueue") Queue queue, @Qualifier("coursesExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(COURSES_ROUTING_KEY);
    }

    // --- Configuration for Enrollment Events ---
    public static final String ENROLLMENTS_EXCHANGE_NAME = "enrollments-exchange";
    public static final String ENROLLMENTS_QUEUE_NAME = "enrollments-notifications-queue";
    public static final String ENROLLMENTS_ROUTING_KEY = "user.enrolled";

    @Bean
    public TopicExchange enrollmentsExchange() {
        return new TopicExchange(ENROLLMENTS_EXCHANGE_NAME);
    }

    @Bean
    public Queue enrollmentsQueue() {
        return new Queue(ENROLLMENTS_QUEUE_NAME, true);
    }

    @Bean
    public Binding enrollmentsBinding(@Qualifier("enrollmentsQueue") Queue queue, @Qualifier("enrollmentsExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ENROLLMENTS_ROUTING_KEY);
    }


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}