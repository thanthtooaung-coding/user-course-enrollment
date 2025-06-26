package com.userenrollment.userservice.controller;

import com.userenrollment.userservice.service.UserService;
import com.userenrollment.userservice.request.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User creation request received and event published!");
    }
}