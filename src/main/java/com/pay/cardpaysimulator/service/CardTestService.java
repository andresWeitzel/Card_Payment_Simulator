package com.pay.cardpaysimulator.service;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.enums.CardStatus;
import com.pay.cardpaysimulator.enums.ScenarioType;
import com.pay.cardpaysimulator.repository.CardRepository;
import com.pay.cardpaysimulator.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardTestService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public List<Card> initializeTestScenarios() {
        // Delete dependent transactions first to avoid FK violations
        transactionRepository.deleteAllInBatch();
        cardRepository.deleteAllInBatch();
        List<Card> cards = List.of(
            // Approval
            createCardWithScenario("4242424242424242", "John Doe", LocalDate.now().plusYears(2), "123", new BigDecimal("10000.00"), ScenarioType.APPROVAL, "US", "USD", "Standard approval test card"),
            createCardWithScenario("5555555555554444", "Jane Smith", LocalDate.now().plusYears(1), "456", new BigDecimal("5000.00"), ScenarioType.APPROVAL, "US", "USD", "Standard approval test card"),

            // Decline
            createCardWithScenario("4000000000000002", "Mark Lee", LocalDate.now().plusYears(2), "789", new BigDecimal("1000.00"), ScenarioType.DECLINE, "US", "USD", "Forced decline test card"),
            createCardWithScenario("4000000000000010", "Sara Johnson", LocalDate.now().plusYears(1), "321", new BigDecimal("2000.00"), ScenarioType.DECLINE, "US", "USD", "Forced decline test card"),
            createCardWithScenario("4000000000009995", "Chris Evans", LocalDate.now().plusYears(2), "123", new BigDecimal("10.00"), ScenarioType.DECLINE, "US", "USD", "Low funds test card"),
            createCardWithScenario("4000000000009987", "Alex Brown", LocalDate.now().plusYears(1), "456", new BigDecimal("5.00"), ScenarioType.DECLINE, "US", "USD", "Low funds test card"),
            createCardWithScenario("4000000000000069", "Tom Harris", LocalDate.now().minusMonths(1), "789", new BigDecimal("1000.00"), ScenarioType.DECLINE, "US", "USD", "Expired card test"),
            createCardWithScenario("4000000000000127", "Eva Lewis", LocalDate.now().minusDays(1), "321", new BigDecimal("2000.00"), ScenarioType.DECLINE, "US", "USD", "Expired card test"),

            // Error
            createCardWithScenario("4000000000000341", "Paul Miller", LocalDate.now().plusYears(2), "456", new BigDecimal("3000.00"), ScenarioType.ERROR, "US", "USD", "Simulated processing error"),
            createCardWithScenario("4000000000000119", "Linda Wilson", LocalDate.now().plusYears(1), "789", new BigDecimal("4000.00"), ScenarioType.ERROR, "US", "USD", "Simulated processing error")
        );

        return cardRepository.saveAll(cards);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Map<String, String>>> getTestScenariosInfo() {
        return Map.of(
            "approval", List.of(
                Map.of(
                    "card_number", "4242424242424242",
                    "cvv", "123",
                    "processing_rule", "APPROVAL",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Standard approval test card",
                    "processing_rule_reason", "Standard approval test card"
                ),
                Map.of(
                    "card_number", "5555555555554444",
                    "cvv", "456",
                    "processing_rule", "APPROVAL",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Standard approval test card",
                    "processing_rule_reason", "Standard approval test card"
                )
            ),
            "decline", List.of(
                Map.of(
                    "card_number", "4000000000000002",
                    "cvv", "789",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Forced decline test card",
                    "processing_rule_reason", "Forced decline test card"
                ),
                Map.of(
                    "card_number", "4000000000000010",
                    "cvv", "321",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Forced decline test card",
                    "processing_rule_reason", "Forced decline test card"
                ),
                Map.of(
                    "card_number", "4000000000009995",
                    "cvv", "123",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Low funds test card",
                    "processing_rule_reason", "Low funds test card"
                ),
                Map.of(
                    "card_number", "4000000000009987",
                    "cvv", "456",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Low funds test card",
                    "processing_rule_reason", "Low funds test card"
                ),
                Map.of(
                    "card_number", "4000000000000069",
                    "cvv", "789",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Expired card test",
                    "processing_rule_reason", "Expired card test"
                ),
                Map.of(
                    "card_number", "4000000000000127",
                    "cvv", "321",
                    "processing_rule", "DECLINE",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Expired card test",
                    "processing_rule_reason", "Expired card test"
                )
            ),
            "error", List.of(
                Map.of(
                    "card_number", "4000000000000341",
                    "cvv", "456",
                    "processing_rule", "ERROR",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Simulated processing error",
                    "processing_rule_reason", "Simulated processing error"
                ),
                Map.of(
                    "card_number", "4000000000000119",
                    "cvv", "789",
                    "processing_rule", "ERROR",
                    "status", "TEST",
                    "country_code", "US",
                    "currency", "USD",
                    "daily_limit_amount", "5000.00",
                    "daily_limit_count", "10",
                    "notes", "Simulated processing error",
                    "processing_rule_reason", "Simulated processing error"
                )
            )
        );
    }

    @Transactional(readOnly = true)
    public boolean isTestScenariosInitialized() {
        return cardRepository.count() > 0;
    }

    private Card createCardWithScenario(String cardNumber, String cardholderName, LocalDate expirationDate, String cvv, BigDecimal balance, ScenarioType scenario, String countryCode, String currency, String notes) {
        return Card.builder()
                .cardNumber(cardNumber)
                .cardholderName(cardholderName)
                .expirationDate(expirationDate)
                .cvv(cvv)
                .balance(balance)
                .processingRule(scenario)
                .status(CardStatus.TEST)
                .countryCode(countryCode)
                .currency(currency)
                .dailyLimitAmount(new BigDecimal("5000.00"))
                .dailyLimitCount(10)
                .creditLimit(new BigDecimal("0.00"))
                .availableCredit(new BigDecimal("0.00"))
                .avsAddressLine1("123 Test St")
                .avsPostalCode("00000")
                .lastUsedAt(LocalDateTime.now())
                .lastFailedAttemptAt(LocalDateTime.now())
                .notes(notes)
                .processingRuleReason(notes)
                .build();
    }
}


