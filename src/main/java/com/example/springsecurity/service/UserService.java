package com.example.springsecurity.service;

import com.example.springsecurity.dto.AuthResponse;
import com.example.springsecurity.dto.RegisterRequest;
import com.example.springsecurity.dto.UserResponse;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.mapper.UserMapper;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.security.CustomUserDetails;
import com.example.springsecurity.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
        User savedUser = userMapper.toEntity(request);
        savedUser.setHashedPassword(passwordEncoder.encode(savedUser.getHashedPassword()));
        userRepository.save(savedUser);
        return userMapper.toResponse(savedUser);
    }

    public AuthResponse login(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getId());
        return new AuthResponse(token);
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
