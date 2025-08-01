package com.codewithmosh.arypto.mappers;

import com.codewithmosh.arypto.dtos.RegisterUserRequest;
import com.codewithmosh.arypto.dtos.UpdateUserRequest;
import com.codewithmosh.arypto.dtos.UserDto;
import com.codewithmosh.arypto.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}
