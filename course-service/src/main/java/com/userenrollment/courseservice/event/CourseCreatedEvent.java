package com.userenrollment.courseservice.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record CourseCreatedEvent(
    String courseId,
    String title,
    String instructor,
    BigDecimal price,
    Instant timestamp
) implements Serializable {}