package com.userenrollment.enrollmentservice.response;

import com.userenrollment.enrollmentservice.model.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private Instant enrollmentDate;

    public static EnrollmentResponse fromEnrollment(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.getId(),
            enrollment.getUserId(),
            enrollment.getCourseId(),
            enrollment.getEnrollmentDate()
        );
    }
}