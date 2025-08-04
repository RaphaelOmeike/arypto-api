package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.dtos.FetchWalletResponse;
import com.codewithmosh.arypto.dtos.WalletDto;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.services.CryptoPaymentGateway;
import com.codewithmosh.arypto.services.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SecurityRequirement(name = "Bearer Authentication") // Only this method is protected
@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
public class WalletController {
    private final CryptoPaymentGateway paymentGateway;
    private final WalletService walletService;

    @PostMapping("/create/{userId}/{crypto}")
    public ResponseEntity<FetchWalletResponse> createWallet(
            @PathVariable(name = "userId") String userId,
            @PathVariable(name = "crypto") String cryptoCurrency) {
        var response = paymentGateway.createPaymentAddress(userId, cryptoCurrency);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{walletId}")
    public ResponseEntity<WalletDto> fetchWalletWithTransactions(@PathVariable(name = "walletId") String walletId) {
        var response = walletService.fetchWalletWithTransactions(walletId);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/deposit/{walletId}")
//    public ResponseEntity<WalletDto> deposit(@PathVariable(name = "walletId") String walletId) {
//        handled by the webhook already
//    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWalletNotFound() {
        return ResponseEntity.status((HttpStatus.NOT_FOUND)).body(
                Map.of("error", "Wallet not found.")
        );
    }

}
