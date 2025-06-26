package com.userenrollment.notificationservice.listener;

import com.userenrollment.notificationservice.config.RabbitMQConfig;
import com.userenrollment.notificationservice.event.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("Received event: {}", event);

        log.info("Processing notification for new product ID: {}...", event.productId());

        log.info("Notification processed successfully for Product ID: {}", event.productId());
    }
}