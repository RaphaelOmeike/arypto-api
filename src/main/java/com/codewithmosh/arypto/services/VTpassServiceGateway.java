package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.AirtimePurchaseRequest;
import com.codewithmosh.arypto.dtos.AirtimePurchaseResponse;
import com.codewithmosh.arypto.dtos.TransactionStatusRequest;
import com.codewithmosh.arypto.dtos.TransactionStatusResponse;
import com.codewithmosh.arypto.entities.DeliveryStatus;
import com.codewithmosh.arypto.exceptions.TransactionNotFoundException;
import com.codewithmosh.arypto.repositories.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VTpassServiceGateway implements UtilityServiceGateway {
    private final TransactionRepository transactionRepository;
    @Value("${vtpass.staticKey}")
    private String staticApiKey;

    @Value("${vtpass.secretKey}")
    private String apiKey;

    @Value("${vtpass.publicKey}")
    private String publicKey;

    @Value("${vtpass.baseUrl}")
    private String baseUrl;

    private static ZoneId LAGOS_ZONE = ZoneId.of("Africa/Lagos");
    private static DateTimeFormatter REQUEST_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @Override
    public String generateRequestId() {
        // Step 1: Get current datetime in Africa/Lagos
        LocalDateTime nowInLagos = LocalDateTime.now(LAGOS_ZONE);

        // Step 2: Format to "YYYYMMDDHHmm"
        String timePart = nowInLagos.format(REQUEST_ID_FORMAT);

        // Step 3: Append random alphanumeric string (e.g., from UUID)
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        // Final request_id
        return timePart + randomPart;
    }

    @Override
    public AirtimePurchaseResponse purchaseAirtime(AirtimePurchaseRequest request) {
        try {
            String url = baseUrl + "/pay";

            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .header("api-key", staticApiKey)
                    .header("secret-key", apiKey)
//                    .header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            var response = gson.fromJson(postResponse.body(), AirtimePurchaseResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to purchase product on VTpass.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public TransactionStatusResponse fetchTransactionStatus(String requestId) {
        try {
            String url = baseUrl + "/requery";  // e.g. usdtngn

            Gson gson = new Gson();

            var request = new TransactionStatusRequest(requestId);
            String jsonRequest = gson.toJson(request);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .header("api-key", staticApiKey)
                    .header("secret-key", apiKey)
//                    .header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            var response = gson.fromJson(postResponse.body(), TransactionStatusResponse.class);
            return response;
        }
        catch (Exception ex) {
            System.out.println("Error: Failed to verify transaction on VTpass.");
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public void processWebhookRequest(String rawPayload) {
        try {
            JsonObject root = JsonParser.parseString(rawPayload).getAsJsonObject();

            String type = root.has("type") ? root.get("type").getAsString() : null;
            if (!"transaction-update".equalsIgnoreCase(type)) {
                System.out.println("Ignored webhook type: " + type);
                return;
            }

            JsonObject data = root.getAsJsonObject("data");
            if (data == null) {
                System.out.println("Missing 'data' object in webhook");
                return;
            }

            JsonObject content = data.getAsJsonObject("content");
            JsonObject transaction = content.getAsJsonObject("transactions");

            String status = transaction.has("status") ? transaction.get("status").getAsString() : null;
            String requestId = data.has("requestId") ? data.get("requestId").getAsString() : null;
            String phone = transaction.has("phone") ? transaction.get("phone").getAsString() : null;
            Double amount = transaction.has("amount") ? transaction.get("amount").getAsDouble() : null;
            String transactionId = transaction.has("transactionId") ? transaction.get("transactionId").getAsString() : null;

            System.out.println("✅ VTpass Transaction Update Received");
            System.out.println("Request ID: " + requestId);
            System.out.println("Status: " + status);
            System.out.println("Phone: " + phone);
            System.out.println("Amount: " + amount);
            System.out.println("Transaction ID: " + transactionId);

            // TODO: Use requestId to locate your local DB record and update its status

            var existingTransaction = transactionRepository.findByRequestId(requestId).orElse(null);

            if (existingTransaction == null) {
                throw new TransactionNotFoundException();
            }

            if (status != null && status.toLowerCase().contains("delivered")) {
                existingTransaction.setDeliveryStatus(DeliveryStatus.FAILED);
                System.out.println("❌ Transaction not delivered, ignoring webhook.");
            }
            else {
                existingTransaction.setDeliveryStatus(DeliveryStatus.DELIVERED);
            }
            existingTransaction.getWallet().setActive(false);
            existingTransaction.setIsTerminated(true);

            transactionRepository.save(existingTransaction); //touch grass
        } catch (Exception ex) {
            System.out.println("❌ Error processing VTpass webhook:");
            ex.printStackTrace();
        }
    }
}
