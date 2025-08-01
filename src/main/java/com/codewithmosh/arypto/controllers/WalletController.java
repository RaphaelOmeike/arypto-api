package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.dtos.FetchWalletResponse;
import com.codewithmosh.arypto.dtos.WalletDto;
import com.codewithmosh.arypto.services.CryptoPaymentGateway;
import com.codewithmosh.arypto.services.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
