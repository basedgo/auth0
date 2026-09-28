package com.example.springsecurity.service;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrAddUser(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getSubject();

        return userRepository.findByAuth0Id(auth0Id)
            .orElseGet(() -> {
                User user = new User();
                user.setAuth0Id(auth0Id);
                user.setEmail(jwt.getClaimAsString("https://spring-security-api/email"));
                return userRepository.save(user);
        });
    }
}
