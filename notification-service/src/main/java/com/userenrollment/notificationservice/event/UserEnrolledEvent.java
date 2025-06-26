package com.userenrollment.notificationservice.event;

import java.io.Serializable;
import java.time.Instant;

public record UserEnrolledEvent(
    String enrollmentId,
    String userId,
    String courseId,
    Instant timestamp
) implements Serializable {}