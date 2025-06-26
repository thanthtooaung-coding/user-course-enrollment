package com.ecommerce.userservice.event;

import java.io.Serializable;
import java.time.Instant;

public record UserRegisteredEvent(
        String userId,
        String username,
        String email,
        Instant timestamp
) implements Serializable {}