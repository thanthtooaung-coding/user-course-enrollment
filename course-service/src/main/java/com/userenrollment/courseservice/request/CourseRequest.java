package com.userenrollment.courseservice.request;

import java.math.BigDecimal;

public record CourseRequest(String title, String instructor, BigDecimal price) {}