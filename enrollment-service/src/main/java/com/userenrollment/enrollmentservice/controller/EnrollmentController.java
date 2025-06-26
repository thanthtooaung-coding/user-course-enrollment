package com.userenrollment.enrollmentservice.controller;

import com.userenrollment.enrollmentservice.service.EnrollmentService;
import com.userenrollment.enrollmentservice.request.EnrollmentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<String> enrollUser(@RequestBody EnrollmentRequest request) {
        enrollmentService.enrollUser(request);
        return ResponseEntity.ok("Enrollment request received and event published!");
    }
}