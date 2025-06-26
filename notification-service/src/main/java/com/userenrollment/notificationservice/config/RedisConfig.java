package com.userenrollment.notificationservice.config;

import com.userenrollment.notificationservice.listener.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    public static final String USER_REGISTERED_TOPIC = "user-registered-topic";
    public static final String COURSE_CREATED_TOPIC = "course-created-topic";
    public static final String USER_ENROLLED_TOPIC = "user-enrolled-topic";

    @Bean
    MessageListenerAdapter messageListener(RedisMessageSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(USER_REGISTERED_TOPIC));
        container.addMessageListener(listenerAdapter, new ChannelTopic(COURSE_CREATED_TOPIC));
        container.addMessageListener(listenerAdapter, new ChannelTopic(USER_ENROLLED_TOPIC));
        return container;
    }
}