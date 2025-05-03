package com.pay.cardpaysimulator.service;

import com.pay.cardpaysimulator.dto.PaymentRequest;
import com.pay.cardpaysimulator.dto.PaymentResponse;
import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.Transaction;
import com.pay.cardpaysimulator.model.TransactionStatus;
import com.pay.cardpaysimulator.repository.CardRepository;
import com.pay.cardpaysimulator.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            Card card = cardRepository.findByCardNumber(request.getCardNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Card not found"));

            if (isCardExpired(card.getExpirationDate())) {
                return createDeclinedResponse("Card is expired");
            }

            if (!card.getCvv().equals(request.getCvv())) {
                return createDeclinedResponse("Invalid CVV");
            }

            if (card.getBalance().compareTo(request.getAmount()) < 0) {
                return createDeclinedResponse("Insufficient funds");
            }

            // Create and save transaction
            Transaction transaction = Transaction.builder()
                    .card(card)
                    .amount(request.getAmount())
                    .status(TransactionStatus.APPROVED)
                    .timestamp(LocalDateTime.now())
                    .description(request.getDescription())
                    .build();

            transactionRepository.save(transaction);

            // Update card balance
            card.setBalance(card.getBalance().subtract(request.getAmount()));
            cardRepository.save(card);

            return PaymentResponse.builder()
                    .status(TransactionStatus.APPROVED)
                    .message("Payment processed successfully")
                    .transactionId(transaction.getId())
                    .build();

        } catch (Exception e) {
            return createFailedResponse("Payment processing failed: " + e.getMessage());
        }
    }

    private boolean isCardExpired(LocalDate expirationDate) {
        return expirationDate.isBefore(LocalDate.now());
    }

    private PaymentResponse createDeclinedResponse(String message) {
        return PaymentResponse.builder()
                .status(TransactionStatus.DECLINED)
                .message(message)
                .build();
    }

    private PaymentResponse createFailedResponse(String message) {
        return PaymentResponse.builder()
                .status(TransactionStatus.FAILED)
                .message(message)
                .build();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCardNumber(String cardNumber) {
        return transactionRepository.findByCardCardNumber(cardNumber);
    }

    @Transactional
    public PaymentResponse processRefund(Long transactionId) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            if (transaction.getStatus() != TransactionStatus.APPROVED) {
                return createDeclinedResponse("Cannot refund a non-approved transaction");
            }

            Card card = transaction.getCard();
            card.setBalance(card.getBalance().add(transaction.getAmount()));
            cardRepository.save(card);

            transaction.setStatus(TransactionStatus.REFUNDED);
            transactionRepository.save(transaction);

            return PaymentResponse.builder()
                    .status(TransactionStatus.REFUNDED)
                    .message("Refund processed successfully")
                    .transactionId(transaction.getId())
                    .build();

        } catch (Exception e) {
            return createFailedResponse("Refund processing failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<String> getTransactionStatus(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> transaction.getStatus().name());
    }
} 