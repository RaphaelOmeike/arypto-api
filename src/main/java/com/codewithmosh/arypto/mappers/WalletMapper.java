package com.codewithmosh.arypto.mappers;

import com.codewithmosh.arypto.dtos.WalletDto;
import com.codewithmosh.arypto.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(source = "user.id", target = "userId")
    WalletDto toDto(Wallet wallet);
}
