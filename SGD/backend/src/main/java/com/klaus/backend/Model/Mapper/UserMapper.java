package com.klaus.backend.Model.Mapper;

import com.klaus.backend.Model.User;
import com.klaus.backend.Model.DTO.UserRequestDTO;
import com.klaus.backend.Model.DTO.UserResponseDTO;

public class UserMapper {
    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setPassword(dto.password());
        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getName());
    }
}