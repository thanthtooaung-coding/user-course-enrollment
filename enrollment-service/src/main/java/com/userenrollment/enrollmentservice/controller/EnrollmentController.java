package com.userenrollment.enrollmentservice.controller;

import com.userenrollment.enrollmentservice.request.EnrollmentRequest;
import com.userenrollment.enrollmentservice.response.EnrollmentResponse;
import com.userenrollment.enrollmentservice.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollUser(@RequestBody EnrollmentRequest request) {
        EnrollmentResponse newEnrollment = enrollmentService.enrollUser(request);
        return ResponseEntity.status(201).body(newEnrollment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable UUID id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}