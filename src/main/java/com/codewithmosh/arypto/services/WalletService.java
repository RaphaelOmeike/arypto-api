package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.TransactionDto;
import com.codewithmosh.arypto.dtos.WalletDto;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.mappers.WalletMapper;
import com.codewithmosh.arypto.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletDto fetchWalletWithTransactions(String walletId) {
        var wallet = walletRepository.getWalletWithTransactions(walletId).orElse(null);

        if (wallet == null) {
            throw new WalletNotFoundException(); // or throw an exception if preferred
        }
        return walletMapper.toDto(wallet);
    }

}
