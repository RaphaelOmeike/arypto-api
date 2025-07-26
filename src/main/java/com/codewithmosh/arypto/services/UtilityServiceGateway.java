package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.AirtimePurchaseRequest;
import com.codewithmosh.arypto.dtos.AirtimePurchaseResponse;
import com.codewithmosh.arypto.dtos.TransactionStatusResponse;

import java.math.BigDecimal;

public interface UtilityServiceGateway {
    String generateRequestId();
    AirtimePurchaseResponse purchaseAirtime(AirtimePurchaseRequest request);
    TransactionStatusResponse fetchTransactionStatus(String requestId);
    public void processWebhookRequest(String rawPayload);
}
