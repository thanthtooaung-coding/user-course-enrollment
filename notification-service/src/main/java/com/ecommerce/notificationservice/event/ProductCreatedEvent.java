package com.userenrollment.notificationservice.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductCreatedEvent(
    String productId,
    String productName,
    BigDecimal price,
    Instant timestamp
) implements Serializable {}