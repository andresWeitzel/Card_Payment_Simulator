package com.pay.cardpaysimulator.controller;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.repository.CardRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for managing payment cards")
public class CardController {

    private final CardRepository cardRepository;

    @PostMapping("/initialize")
    @Operation(summary = "Initialize valid test cards", description = "Creates a set of valid test cards with different balances")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards initialized successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Card>> initializeValidCards() {
        try {
            // Clear existing cards
            cardRepository.deleteAll();

            // Create and save valid test cards using official test numbers
            List<Card> cards = List.of(
                createCard("4242424242424242", "John Doe", LocalDate.now().plusYears(2), "123", new BigDecimal("1000.00")),
                createCard("5555555555554444", "Jane Smith", LocalDate.now().plusYears(1), "456", new BigDecimal("500.00")),
                createCard("378282246310005", "Bob Johnson", LocalDate.now().plusMonths(6), "789", new BigDecimal("2000.00")),
                createCard("6011111111111117", "Alice Brown", LocalDate.now().plusYears(3), "321", new BigDecimal("750.00"))
            );

            List<Card> savedCards = cardRepository.saveAll(cards);
            return ResponseEntity.ok(savedCards);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/initialize-test-scenarios")
    @Operation(summary = "Initialize test scenario cards", description = "Creates a set of official test cards with different scenarios (approval, decline, error)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test scenario cards initialized successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Card>> initializeTestScenarioCards() {
        try {
            // Clear existing cards
            cardRepository.deleteAll();

            // Create and save test scenario cards
            List<Card> cards = List.of(
                // Always approved cards
                createCard("4242424242424242", "Always Approved", LocalDate.now().plusYears(2), "123", new BigDecimal("10000.00")),
                createCard("5555555555554444", "Always Approved", LocalDate.now().plusYears(1), "456", new BigDecimal("5000.00")),
                
                // Always declined cards
                createCard("4000000000000002", "Always Declined", LocalDate.now().plusYears(2), "789", new BigDecimal("1000.00")),
                createCard("4000000000000010", "Always Declined", LocalDate.now().plusYears(1), "321", new BigDecimal("2000.00")),
                
                // Error scenario cards
                createCard("4000000000000341", "Processing Error", LocalDate.now().plusYears(2), "456", new BigDecimal("3000.00")),
                createCard("4000000000000119", "Processing Error", LocalDate.now().plusYears(1), "789", new BigDecimal("4000.00")),
                
                // Insufficient funds cards
                createCard("4000000000009995", "Insufficient Funds", LocalDate.now().plusYears(2), "123", new BigDecimal("10.00")),
                createCard("4000000000009987", "Insufficient Funds", LocalDate.now().plusYears(1), "456", new BigDecimal("5.00")),
                
                // Expired cards
                createCard("4000000000000069", "Expired Card", LocalDate.now().minusMonths(1), "789", new BigDecimal("1000.00")),
                createCard("4000000000000127", "Expired Card", LocalDate.now().minusDays(1), "321", new BigDecimal("2000.00"))
            );

            List<Card> savedCards = cardRepository.saveAll(cards);
            return ResponseEntity.ok(savedCards);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new card", description = "Creates a new payment card with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid card details"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        try {
            Card savedCard = cardRepository.save(card);
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all cards", description = "Retrieves all cards in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Card>> getAllCards() {
        try {
            log.info("Fetching all cards from database");
            List<Card> cards = cardRepository.findAll();
            log.info("Found {} cards", cards.size());
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{cardNumber}")
    @Operation(summary = "Get card by number", description = "Retrieves a specific card by its card number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Card> getCardByNumber(@PathVariable String cardNumber) {
        try {
            log.info("Fetching card with number: {}", cardNumber);
            return cardRepository.findByCardNumber(cardNumber)
                    .map(card -> {
                        log.info("Card found: {}", card);
                        return ResponseEntity.ok(card);
                    })
                    .orElseGet(() -> {
                        log.warn("Card not found with number: {}", cardNumber);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error fetching card with number: {}", cardNumber, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-scenarios")
    @Operation(summary = "Get test card scenarios information", description = "Retrieves information about available test card scenarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test scenarios information retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, List<Map<String, String>>>> getTestScenarios() {
        try {
            log.info("Fetching test scenarios information");
            Map<String, List<Map<String, String>>> scenarios = Map.of(
                "always_approved", List.of(
                    Map.of(
                        "card_number", "4242424242424242",
                        "description", "Visa card that will always be approved",
                        "cvv", "123",
                        "expiry", "12/25"
                    ),
                    Map.of(
                        "card_number", "5555555555554444",
                        "description", "Mastercard that will always be approved",
                        "cvv", "456",
                        "expiry", "12/25"
                    )
                ),
                "always_declined", List.of(
                    Map.of(
                        "card_number", "4000000000000002",
                        "description", "Visa card that will always be declined",
                        "cvv", "789",
                        "expiry", "12/25"
                    ),
                    Map.of(
                        "card_number", "4000000000000010",
                        "description", "Visa card that will always be declined",
                        "cvv", "321",
                        "expiry", "12/25"
                    )
                ),
                "processing_error", List.of(
                    Map.of(
                        "card_number", "4000000000000341",
                        "description", "Visa card that will trigger a processing error",
                        "cvv", "456",
                        "expiry", "12/25"
                    ),
                    Map.of(
                        "card_number", "4000000000000119",
                        "description", "Visa card that will trigger a processing error",
                        "cvv", "789",
                        "expiry", "12/25"
                    )
                ),
                "insufficient_funds", List.of(
                    Map.of(
                        "card_number", "4000000000009995",
                        "description", "Visa card that will always have insufficient funds",
                        "cvv", "123",
                        "expiry", "12/25"
                    ),
                    Map.of(
                        "card_number", "4000000000009987",
                        "description", "Visa card that will always have insufficient funds",
                        "cvv", "456",
                        "expiry", "12/25"
                    )
                ),
                "expired_cards", List.of(
                    Map.of(
                        "card_number", "4000000000000069",
                        "description", "Visa card that is expired",
                        "cvv", "789",
                        "expiry", "01/23"
                    ),
                    Map.of(
                        "card_number", "4000000000000127",
                        "description", "Visa card that is expired",
                        "cvv", "321",
                        "expiry", "01/23"
                    )
                )
            );
            log.info("Test scenarios information retrieved successfully");
            return ResponseEntity.ok(scenarios);
        } catch (Exception e) {
            log.error("Error fetching test scenarios information", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private Card createCard(String cardNumber, String cardholderName, LocalDate expirationDate, String cvv, BigDecimal balance) {
        return Card.builder()
                .cardNumber(cardNumber)
                .cardholderName(cardholderName)
                .expirationDate(expirationDate)
                .cvv(cvv)
                .balance(balance)
                .build();
    }
} 