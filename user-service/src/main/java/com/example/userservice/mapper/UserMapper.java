package com.example.userservice.mapper;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRequestDto;
import com.example.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto requestDto);
}
