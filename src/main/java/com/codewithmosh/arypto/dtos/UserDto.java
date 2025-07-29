package com.codewithmosh.arypto.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDto {
//    @JsonIgnore
    @JsonProperty("user_id")
    private Long id;
    private String name;
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
