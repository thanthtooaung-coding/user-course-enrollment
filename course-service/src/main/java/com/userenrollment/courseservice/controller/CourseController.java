package com.userenrollment.courseservice.controller;

import com.userenrollment.courseservice.service.CourseService;
import com.userenrollment.courseservice.request.CourseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody CourseRequest request) {
        courseService.createCourse(request);
        return ResponseEntity.ok("Course creation request received and event published!");
    }
}