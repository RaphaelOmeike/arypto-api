package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.AirtimePurchaseResponse;
import com.codewithmosh.arypto.dtos.BuyAirtimeRequest;
import com.codewithmosh.arypto.dtos.TransactionDto;
import com.codewithmosh.arypto.entities.DeliveryStatus;
import com.codewithmosh.arypto.entities.Transaction;
import com.codewithmosh.arypto.entities.TransactionStatus;
import com.codewithmosh.arypto.entities.TransactionType;
import com.codewithmosh.arypto.exceptions.InsufficientBalanceException;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.mappers.TransactionMapper;
import com.codewithmosh.arypto.repositories.TransactionRepository;
import com.codewithmosh.arypto.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {
    private final CryptoPaymentGateway paymentGateway;
    private final UtilityServiceGateway serviceGateway;

    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;


    public AirtimePurchaseResponse purchaseAirtime(BuyAirtimeRequest request) {
        var airtimeRequest = transactionMapper.toAirtimePurchaseRequest(request);
        airtimeRequest.setRequestId(serviceGateway.generateRequestId());

        var existingWallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(WalletNotFoundException::new);
        if (existingWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }
        var initialBalance = existingWallet.getBalance();
        existingWallet.setBalance(initialBalance.subtract(request.getAmount()));

        var response = serviceGateway.purchaseAirtime(airtimeRequest);
        var transaction = Transaction.builder()
                .amountNaira(airtimeRequest.getAmount())
                .requestId(airtimeRequest.getRequestId())
                .transactionType(TransactionType.DEBIT)
                .phoneNumber(request.getPhone())
                .deliveryStatus(DeliveryStatus.PENDING)
                .wallet(existingWallet)
                .transactionStatus(TransactionStatus.PAID)
                .build();

        transactionRepository.save(transaction);
        return response;
    }


//
}

