package com.codewithmosh.arypto.mappers;

import com.codewithmosh.arypto.dtos.AirtimePurchaseRequest;
import com.codewithmosh.arypto.dtos.BuyAirtimeRequest;
import com.codewithmosh.arypto.dtos.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "network", target = "serviceId")
    @Mapping(target = "requestId", ignore = true)
    AirtimePurchaseRequest toAirtimePurchaseRequest(BuyAirtimeRequest request);

    TransactionDto toDto(TransactionDto transaction);
}
