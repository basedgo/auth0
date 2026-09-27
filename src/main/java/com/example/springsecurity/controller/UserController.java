package com.example.springsecurity.controller;

import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.example.springsecurity.dto.UserResponse;
import com.example.springsecurity.mapper.UserMapper;
import com.example.springsecurity.service.UserService;
import com.example.springsecurity.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public UserService userService;
    public UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("sub", jwt.getSubject(), "claims", jwt.getClaims());
    }

    @GetMapping("/add")
    public UserResponse addUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.findOrAddUser(jwt);
        return userMapper.toResponse(user);
    }
}
