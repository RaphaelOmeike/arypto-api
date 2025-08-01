package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.dtos.AirtimePurchaseResponse;
import com.codewithmosh.arypto.dtos.BuyAirtimeRequest;
import com.codewithmosh.arypto.exceptions.InsufficientBalanceException;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/purchase/airtime")
    ResponseEntity<AirtimePurchaseResponse> purchaseAirtime(@RequestBody BuyAirtimeRequest request) {
        var response = transactionService.purchaseAirtime(request);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalance() {
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(
                Map.of("error", "Insufficient balance to complete the transaction.")
        );
    }



    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWalletNotFound() {
        return ResponseEntity.status((HttpStatus.NOT_FOUND)).body(
                Map.of("error", "Wallet not found.")
        );
    }
}
