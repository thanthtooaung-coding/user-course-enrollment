package com.userenrollment.notificationservice.event;

import java.io.Serializable;
import java.math.BigDecimal;

public record CourseCreatedEvent(
    String courseId,
    String title,
    String instructor,
    BigDecimal price
) implements Serializable {}