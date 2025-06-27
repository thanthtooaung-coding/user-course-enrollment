package com.userenrollment.courseservice.response;

import com.userenrollment.courseservice.model.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private UUID id;
    private String title;
    private String instructor;
    private BigDecimal price;

    public static CourseResponse fromCourse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getInstructor(),
                course.getPrice()
        );
    }
}