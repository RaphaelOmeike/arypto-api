package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.*;
import com.codewithmosh.arypto.entities.Wallet;
import com.codewithmosh.arypto.repositories.UserRepository;
import com.codewithmosh.arypto.repositories.WalletRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class QuidaxPaymentGateway implements CryptoPaymentGateway {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    @Value("${quidax.baseUrl}")
    private String baseUrl;

    @Value("${quidax.secretKey}")
    private String apiKey;

    public QuidaxPaymentGateway(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public CreateSubaccountResponse createSubaccount(CreateSubaccountRequest request) {
        try {
            String url = baseUrl + "/users";  // e.g. usdtngn
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            var response = gson.fromJson(postResponse.body(), CreateSubaccountResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to create Quidax sub-account.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public TickerResponse getBuyPrice(String marketPair) {
        try {
            String url = baseUrl + "/markets/tickers/" + marketPair.toLowerCase();  // e.g. usdtngn

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Your API Key")
                    .header("accept", "application/json")
                    .GET()
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            var response = gson.fromJson(getResponse.body(), TickerResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to fetch Quidax buy price for market.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public FetchWalletResponse fetchPaymentAddress(String userId, String cryptoCurrency) {
        try {
            String url = baseUrl + "/users/" + userId + "/wallets/" + cryptoCurrency.toLowerCase() + "/address";  // e.g. usdtngn

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .GET()
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            var response = gson.fromJson(getResponse.body(), FetchWalletResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to fetch Quidax wallet details for wallet.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public FetchWalletResponse createPaymentAddress(String userId, String cryptoCurrency) {
        try {
            String url = baseUrl + "/users/" + userId + "/wallets/" + cryptoCurrency.toLowerCase() + "/addresses?";  // e.g. usdtngn
            var request = new CreatePaymentAddressRequest(userId, cryptoCurrency.toLowerCase());
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            var response = gson.fromJson(postResponse.body(), FetchWalletResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to create Quidax wallet for user.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public void processWebhook(String rawPayload) {
        try {
            JsonObject root = JsonParser.parseString(rawPayload).getAsJsonObject();
            String event = root.has("event") ? root.get("event").getAsString() : null;

            if ("deposit.transaction.confirmation".equalsIgnoreCase(event)) {
                JsonObject data = root.getAsJsonObject("data");

                String id = data.has("id") ? data.get("id").getAsString() : null;
                String txid = data.has("txid") ? data.get("txid").getAsString() : null;
                String currency = data.has("currency") ? data.get("currency").getAsString() : null;
                BigDecimal amount = data.has("amount") ? data.get("amount").getAsBigDecimal() : BigDecimal.valueOf(0.0);

                String depositAddress = null;
                String walletId = null;
                if (data.has("wallet")) {
                    JsonObject wallet = data.getAsJsonObject("wallet");
                    depositAddress = wallet.has("deposit_address") ? wallet.get("deposit_address").getAsString() : null;
                    walletId = wallet.has("id") ? wallet.get("id").getAsString() : null;
                }
//                continue from here
                // create a transaction of type deposit
//                var transaction = T
//                the wallet balance shouldbe maintained locally and not imitated
//                incrememnt wallet balance
                // TODO: use id, txid, currency, amount, depositAddress to verify and credit user
            } else if ("wallet.address.generated".equalsIgnoreCase(event)) {
                JsonObject data = root.getAsJsonObject("data");

                String walletId = data.has("id") ? data.get("id").getAsString() : null;
                String currency = data.has("currency") ? data.get("currency").getAsString() : null;
                String network = data.has("network") ? data.get("network").getAsString() : null;
                String depositAddress = data.has("address") ? data.get("address").getAsString() : null;
                String balance = data.has("balance") ? data.get("balance").getAsString() : null;
                var wallet = Wallet.builder()
                        .id(walletId)
                        .cryptoCurrency(currency)
                        .network(network)
                        .depositAddress(depositAddress)
                        .balance(new BigDecimal(balance != null ? balance : "0.0"))
                        .build();

                String userId = null;
                String userEmail = null;

                if (data.has("user")) {
                    JsonObject user = data.getAsJsonObject("user");
                    userId = user.has("id") ? user.get("id").getAsString() : null;
                    userEmail = user.has("email") ? user.get("email").getAsString() : null;

                    if (userId == null) {
                        System.out.println("userId is null ");
                        return;
                    }
                    var existingUser = userRepository.findById(userId).orElse(null);
                    if (existingUser == null) {
                        System.out.println("User not found");
                        return;
                    }
                    wallet.setUser(existingUser);
                }

                walletRepository.save(wallet);
                System.out.println("✅ Wallet Address Generated");
                System.out.println("Wallet ID: " + walletId);
                System.out.println("Currency: " + currency);
                System.out.println("Network: " + network);
                System.out.println("Deposit Address: " + depositAddress);
                System.out.println("User ID: " + userId);
                System.out.println("User Email: " + userEmail);
            } else {
                System.out.println("Ignored event: " + event);
            }
        } catch (Exception ex) {
            System.out.println("❌ Error processing webhook:");
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public BigDecimal getCryptoPrice(BigDecimal cryptoBuyPrice, BigDecimal amountNaira) {
        cryptoBuyPrice = cryptoBuyPrice.add(cryptoBuyPrice.multiply(BigDecimal.valueOf(0.01)));
        return amountNaira.divide(cryptoBuyPrice, 8, RoundingMode.CEILING);
    }
}
