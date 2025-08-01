package com.codewithmosh.arypto.dtos;

import com.codewithmosh.arypto.entities.Role;
import com.codewithmosh.arypto.entities.Wallet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
public class UserDto {
//    @JsonIgnore
    @JsonProperty("user_id")
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String phoneNumber;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<WalletDto> cryptoWallets;
}
