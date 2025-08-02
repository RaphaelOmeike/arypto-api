package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.data.AppContext;
import com.codewithmosh.arypto.dtos.AirtimePurchaseResponse;
import com.codewithmosh.arypto.dtos.BuyAirtimeRequest;
import com.codewithmosh.arypto.exceptions.InsufficientBalanceException;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.services.TransactionService;
import com.codewithmosh.arypto.services.UtilityServiceGateway;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UtilityServiceGateway serviceGateway;
    @PostMapping("/purchase/airtime")
    ResponseEntity<AirtimePurchaseResponse> purchaseAirtime(@RequestBody BuyAirtimeRequest request) {
        var response = transactionService.purchaseAirtime(request);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify/{request_id}")
    public ResponseEntity<?> verifyPurchase(
            @PathVariable("request_id") String requestId
    ) {
        var response = serviceGateway.fetchTransactionStatus(requestId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/networks")
    public Map<String, List<String>> getAvailableNetworks() {
        return Map.of("networks", AppContext.networks);
    }

    @GetMapping("/crypto-currencies")
    public Map<String, List<String>> getAvailableCryptoCoins() {
        return Map.of("crypto_currencies", AppContext.cryptos);
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
