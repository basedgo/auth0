package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.UserResponse;
import com.example.springsecurity.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
