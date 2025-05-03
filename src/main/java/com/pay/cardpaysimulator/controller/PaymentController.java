package com.pay.cardpaysimulator.controller;

import com.pay.cardpaysimulator.dto.PaymentRequest;
import com.pay.cardpaysimulator.dto.PaymentResponse;
import com.pay.cardpaysimulator.model.Transaction;
import com.pay.cardpaysimulator.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Processing", description = "Endpoints for processing payments and managing transactions")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process a payment", description = "Processes a payment with the provided card details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        try {
            log.info("Processing payment request: {}", request);
            PaymentResponse response = paymentService.processPayment(request);
            log.info("Payment processed with status: {}", response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing payment", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions", description = "Retrieves all payment transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            log.info("Fetching all transactions");
            List<Transaction> transactions = paymentService.getAllTransactions();
            log.info("Found {} transactions", transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching transactions", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/transactions/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long transactionId) {
        try {
            log.info("Fetching transaction with ID: {}", transactionId);
            return paymentService.getTransactionById(transactionId)
                    .map(transaction -> {
                        log.info("Transaction found: {}", transaction);
                        return ResponseEntity.ok(transaction);
                    })
                    .orElseGet(() -> {
                        log.warn("Transaction not found with ID: {}", transactionId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching transaction with ID: {}", transactionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/transactions/card/{cardNumber}")
    @Operation(summary = "Get transactions by card number", description = "Retrieves all transactions for a specific card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No transactions found for the card"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Transaction>> getTransactionsByCardNumber(@PathVariable String cardNumber) {
        try {
            log.info("Fetching transactions for card: {}", cardNumber);
            List<Transaction> transactions = paymentService.getTransactionsByCardNumber(cardNumber);
            if (transactions.isEmpty()) {
                log.warn("No transactions found for card: {}", cardNumber);
                return ResponseEntity.notFound().build();
            }
            log.info("Found {} transactions for card: {}", transactions.size(), cardNumber);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching transactions for card: {}", cardNumber, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/refund/{transactionId}")
    @Operation(summary = "Process a refund", description = "Processes a refund for a specific transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refund request"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> processRefund(@PathVariable Long transactionId) {
        try {
            log.info("Processing refund for transaction ID: {}", transactionId);
            PaymentResponse response = paymentService.processRefund(transactionId);
            log.info("Refund processed with status: {}", response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing refund for transaction ID: {}", transactionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{transactionId}")
    @Operation(summary = "Get transaction status", description = "Retrieves the current status of a transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> getTransactionStatus(@PathVariable Long transactionId) {
        try {
            log.info("Fetching status for transaction ID: {}", transactionId);
            return paymentService.getTransactionStatus(transactionId)
                    .map(status -> {
                        log.info("Transaction status: {}", status);
                        return ResponseEntity.ok(status);
                    })
                    .orElseGet(() -> {
                        log.warn("Transaction not found with ID: {}", transactionId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching status for transaction ID: {}", transactionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 