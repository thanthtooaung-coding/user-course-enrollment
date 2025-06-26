package com.userenrollment.notificationservice.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record UserCreatedEvent(
    String userId,
    String userName,
    BigDecimal price,
    Instant timestamp
) implements Serializable {}