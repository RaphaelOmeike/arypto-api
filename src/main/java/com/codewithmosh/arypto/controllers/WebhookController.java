package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.dtos.ErrorDto;
import com.codewithmosh.arypto.exceptions.TransactionNotFoundException;
import com.codewithmosh.arypto.exceptions.WalletNotFoundException;
import com.codewithmosh.arypto.services.CryptoPaymentGateway;
import com.codewithmosh.arypto.services.UtilityServiceGateway;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final CryptoPaymentGateway paymentGateway;
    private final UtilityServiceGateway serviceGateway;

    // for testing only: cleartext/simple secret. Replace in prod.
    @Value("${quidax.webhookSecret}")
    private String QUIDAX_WEBHOOK_SECRET;
    // allow 5 minutes drift
    private static final long TOLERANCE_SECONDS = 300;

    @PostMapping("/quidax")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws IOException {
        String rawPayload = readRawBody(request);
        String signatureHeader = request.getHeader("quidax-signature");
        if (signatureHeader == null || signatureHeader.isBlank()) {
            log.warn("Missing quidax-signature header");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing signature header");
        }

        if (!isValidQuidaxSignature(signatureHeader, rawPayload)) {
            log.warn("Invalid Quidax signature. Header: {}", signatureHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        // Signature verified; proceed.
        paymentGateway.processWebhook(rawPayload);
        return ResponseEntity.ok("Webhook processed");
    }

    private String readRawBody(HttpServletRequest request) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        return jsonBuilder.toString();
    }

    /**
     * Verifies the Quidax signature according to their docs:
     * Header format: "t=<timestamp>,s=<signature>"
     * Payload to sign: "<timestamp>.<rawBody>"
     * HMAC-SHA256 with the shared secret.
     */
    private boolean isValidQuidaxSignature(String header, String rawBody) {
        try {
            String[] parts = header.split(",");
            if (parts.length != 2) return false;

            String[] tsPart = parts[0].split("=", 2);
            String[] sigPart = parts[1].split("=", 2);
            if (tsPart.length != 2 || sigPart.length != 2) return false;
            if (!"t".equals(tsPart[0]) || !"s".equals(sigPart[0])) return false;

            long timestamp = Long.parseLong(tsPart[1]);
            String receivedSignature = sigPart[1];

            long now = Instant.now().getEpochSecond();
            if (Math.abs(now - timestamp) > TOLERANCE_SECONDS) {
                log.warn("Timestamp outside tolerance. Now: {}, Received: {}", now, timestamp);
                return false; // possible replay or skew
            }

            String payloadToSign = timestamp + "." + rawBody;
            byte[] computedHmac = computeHmacSha256(payloadToSign, QUIDAX_WEBHOOK_SECRET);
            String expectedHex = bytesToHex(computedHmac);

            // Constant-time comparison
            boolean matches = MessageDigest.isEqual(expectedHex.getBytes(StandardCharsets.UTF_8),
                    receivedSignature.getBytes(StandardCharsets.UTF_8));
            if (!matches) {
                log.debug("Expected signature: {}, received: {}", expectedHex, receivedSignature);
            }
            return matches;
        } catch (Exception e) {
            log.error("Exception while verifying Quidax signature", e);
            return false;
        }
    }

    private byte[] computeHmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @PostMapping("/vtpass")
    public Map<String, String> handleVTPassWebhook(HttpServletRequest request) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        String rawPayload = jsonBuilder.toString();
        serviceGateway.processWebhookRequest(rawPayload);

        return Map.of("response", "success");
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTransactionNotFound(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleWalletNotFound(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Wallet not found: "));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDto> handleIO(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(ex.getMessage()));
    }
}
