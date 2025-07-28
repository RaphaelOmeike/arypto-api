package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.*;

import java.math.BigDecimal;

public interface CryptoPaymentGateway {
    public CreateSubaccountResponse createSubaccount(CreateSubaccountRequest request);
    public TickerResponse getBuyPrice(String marketPair);
    public FetchWalletResponse fetchPaymentAddress(String userId, String cryptoCurrency);
    public FetchWalletResponse createPaymentAddress(String userId, String cryptoCurrency);
    public void processWebhook(String rawPayload);
    BigDecimal getCryptoPrice(BigDecimal cryptoBuyPrice, BigDecimal amountNaira);
}
