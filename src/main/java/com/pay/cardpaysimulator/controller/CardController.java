package com.pay.cardpaysimulator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.pay.cardpaysimulator.constants.ApiResponseCards;
import com.pay.cardpaysimulator.dto.*;
import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.enums.CardBrand;
import com.pay.cardpaysimulator.enums.CardStatus;
import com.pay.cardpaysimulator.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for managing payment cards")
public class CardController {

    private final CardService cardService;

    private Map<String, Object> errorBody(int status, String error, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("error", error);
        m.put("message", message);
        return m;
    }

    // ==================== GET ENDPOINTS ====================

    @GetMapping
    @Operation(summary = "Get all cards", description = "Retrieves all cards in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_CARDS)))
    })
    public ResponseEntity<?> getAllCards() {
        try {
            log.info("Fetching all cards from database");
            List<Card> cards = cardService.getAllCards();
            log.info("Found {} cards", cards.size());
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards", e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching cards"));
        }
    }

    @GetMapping("/{cardNumber}")
    @Operation(summary = "Get card by number", description = "Retrieves a specific card by its card number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_CARD)))
    })
    public ResponseEntity<?> getCardByNumber(@PathVariable String cardNumber) {
        try {
            log.info("Fetching card with number: {}", cardNumber);
            Optional<Card> cardOpt = cardService.getByCardNumber(cardNumber);
            if (cardOpt.isPresent()) {
                return ResponseEntity.ok(cardOpt.get());
            }
            log.warn("Card not found with number: {}", cardNumber);
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error fetching card with number: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching card"));
        }
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get cards by status", description = "Retrieves all cards with the specified status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "status-bad-request", value = ApiResponseCards.ERROR_400_INVALID_STATUS))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_STATUS)))
    })
    public ResponseEntity<?> getCardsByStatus(@PathVariable CardStatus status) {
        try {
            log.info("Fetching cards with status: {}", status);
            List<Card> cards = cardService.getByStatus(status);
            log.info("Found {} cards with status: {}", cards.size(), status);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by status: {}", status, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid status"));
        }
    }

    @GetMapping("/by-brand/{brand}")
    @Operation(summary = "Get cards by brand", description = "Retrieves all cards with the specified brand (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid brand",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "brand-bad-request", value = ApiResponseCards.ERROR_400_INVALID_BRAND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_BRAND)))
    })
    public ResponseEntity<?> getCardsByBrand(@PathVariable String brand) {
        try {
            log.info("Fetching cards with brand: {}", brand);
            CardBrand cardBrand;
            try {
                cardBrand = CardBrand.valueOf(brand.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid brand: {}", brand);
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid brand. Valid values are: VISA, MASTERCARD, AMEX, DISCOVER"));
            }
            List<Card> cards = cardService.getByBrand(cardBrand);
            log.info("Found {} cards with brand: {}", cards.size(), cardBrand);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by brand: {}", brand, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid brand"));
        }
    }

    @GetMapping("/by-bin/{bin}")
    @Operation(summary = "Get cards by BIN", description = "Retrieves all cards with the specified BIN (Bank Identification Number)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "cards-found", value = ApiResponseCards.EXAMPLE_CARDS_FOUND),
                    @ExampleObject(name = "no-cards", value = ApiResponseCards.EXAMPLE_NO_CARDS_BIN)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_BIN)))
    })
    public ResponseEntity<?> getCardsByBin(@PathVariable String bin) {
        try {
            log.info("Fetching cards with BIN: {}", bin);
            List<Card> cards = cardService.getByBin(bin);
            log.info("Found {} cards with BIN: {}", cards.size(), bin);
            if (cards.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "No cards found with BIN: " + bin);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by BIN: {}", bin, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching cards by BIN"));
        }
    }

    @GetMapping("/by-last4/{last4}")
    @Operation(summary = "Get cards by last 4 digits", description = "Retrieves all cards with the specified last 4 digits")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "cards-found", value = ApiResponseCards.EXAMPLE_CARDS_FOUND),
                    @ExampleObject(name = "no-cards", value = ApiResponseCards.EXAMPLE_NO_CARDS_LAST4)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_LAST4)))
    })
    public ResponseEntity<?> getCardsByLast4(@PathVariable String last4) {
        try {
            log.info("Fetching cards with last4: {}", last4);
            List<Card> cards = cardService.getByLast4(last4);
            log.info("Found {} cards with last4: {}", cards.size(), last4);
            if (cards.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "No cards found with last4: " + last4);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by last4: {}", last4, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching cards by last4"));
        }
    }

    @GetMapping("/by-country/{countryCode}")
    @Operation(summary = "Get cards by country code", description = "Retrieves all cards with the specified country code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_COUNTRY)))
    })
    public ResponseEntity<?> getCardsByCountryCode(@PathVariable String countryCode) {
        try {
            log.info("Fetching cards with country code: {}", countryCode);
            List<Card> cards = cardService.getByCountryCode(countryCode);
            log.info("Found {} cards with country code: {}", cards.size(), countryCode);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by country code: {}", countryCode, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching cards by country"));
        }
    }

    @GetMapping("/by-currency/{currency}")
    @Operation(summary = "Get cards by currency", description = "Retrieves all cards with the specified currency")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "cards-found", value = ApiResponseCards.EXAMPLE_CARDS_FOUND),
                    @ExampleObject(name = "no-cards", value = ApiResponseCards.EXAMPLE_NO_CARDS_CURRENCY)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_FETCHING_BY_CURRENCY)))
    })
    public ResponseEntity<?> getCardsByCurrency(@PathVariable String currency) {
        try {
            log.info("Fetching cards with currency: {}", currency);
            List<Card> cards = cardService.getByCurrency(currency);
            log.info("Found {} cards with currency: {}", cards.size(), currency);
            if (cards.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "No cards found with currency: " + currency);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error fetching cards by currency: {}", currency, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching cards by currency"));
        }
    }

    // ==================== POST ENDPOINTS ====================

    @PostMapping
    @Operation(summary = "Create a new card", description = "Creates a new payment card with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid card details",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "card-bad-request", value = ApiResponseCards.ERROR_400_INVALID_CARD_DETAILS))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_CREATING_CARD)))
    })
    public ResponseEntity<?> createCard(@Valid @RequestBody Card card) {
        try {
            Card savedCard = cardService.createCard(card);
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            log.error("Error creating card", e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid card details"));
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Search cards with multiple filters", description = "Searches for cards using multiple optional filters (bin, last4, countryCode, currency, status, brand). All string filters are case-insensitive.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid brand in search request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = ApiResponseCards.ERROR_400_INVALID_BRAND_VALUES))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_SEARCHING_CARDS)))
    })
    public ResponseEntity<?> searchCards(@RequestBody CardSearchRequest searchRequest) {
        try {
            log.info("Searching cards with filters: {}", searchRequest);
            
            // Convert brand string to enum if provided (case-insensitive)
            CardBrand brand = null;
            if (searchRequest.getBrand() != null && !searchRequest.getBrand().isEmpty()) {
                try {
                    brand = CardBrand.valueOf(searchRequest.getBrand().toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid brand in search request: {}", searchRequest.getBrand());
                    return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid brand. Valid values are: VISA, MASTERCARD, AMEX, DISCOVER"));
                }
            }
            
            List<Card> cards = cardService.searchCards(
                searchRequest.getBin(),
                searchRequest.getLast4(),
                searchRequest.getCountryCode(),
                searchRequest.getCurrency(),
                searchRequest.getStatus(),
                brand
            );
            log.info("Found {} cards matching search criteria", cards.size());
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("Error searching cards", e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while searching cards"));
        }
    }

    @PostMapping("/{cardNumber}/block")
    @Operation(summary = "Block card", description = "Blocks a card by setting its status to BLOCKED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card blocked successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_BLOCKING_CARD)))
    })
    public ResponseEntity<?> blockCard(@PathVariable String cardNumber) {
        try {
            log.info("Blocking card with number: {}", cardNumber);
            Optional<Card> updated = cardService.blockCard(cardNumber);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error blocking card with number: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while blocking card"));
        }
    }

    @PostMapping("/{cardNumber}/unblock")
    @Operation(summary = "Unblock card", description = "Unblocks a card by setting its status to ACTIVE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card unblocked successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UNBLOCKING_CARD)))
    })
    public ResponseEntity<?> unblockCard(@PathVariable String cardNumber) {
        try {
            log.info("Unblocking card with number: {}", cardNumber);
            Optional<Card> updated = cardService.unblockCard(cardNumber);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error unblocking card with number: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while unblocking card"));
        }
    }

    @PostMapping("/{cardNumber}/increment-cvv-attempts")
    @Operation(summary = "Increment failed CVV attempts", description = "Increments the failed CVV attempts counter and updates the last failed attempt timestamp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Failed CVV attempts incremented successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_INCREMENTING_CVV)))
    })
    public ResponseEntity<?> incrementFailedCvvAttempts(@PathVariable String cardNumber) {
        try {
            log.info("Incrementing failed CVV attempts for card: {}", cardNumber);
            Optional<Card> updated = cardService.incrementFailedCvvAttempts(cardNumber);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error incrementing failed CVV attempts for card: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while incrementing CVV attempts"));
        }
    }

    @PostMapping("/{cardNumber}/reset-cvv-attempts")
    @Operation(summary = "Reset failed CVV attempts", description = "Resets the failed CVV attempts counter to 0 and clears the last failed attempt timestamp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Failed CVV attempts reset successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_RESETTING_CVV)))
    })
    public ResponseEntity<?> resetFailedCvvAttempts(@PathVariable String cardNumber) {
        try {
            log.info("Resetting failed CVV attempts for card: {}", cardNumber);
            Optional<Card> updated = cardService.resetFailedCvvAttempts(cardNumber);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error resetting failed CVV attempts for card: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while resetting CVV attempts"));
        }
    }

    @PostMapping("/{cardNumber}/update-last-used")
    @Operation(summary = "Update last used timestamp", description = "Updates the last used timestamp for a card to the current time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Last used timestamp updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_LAST_USED)))
    })
    public ResponseEntity<?> updateLastUsedAt(@PathVariable String cardNumber) {
        try {
            log.info("Updating last used timestamp for card: {}", cardNumber);
            Optional<Card> updated = cardService.updateLastUsedAt(cardNumber);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating last used timestamp for card: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while updating last used timestamp"));
        }
    }

    // ==================== PUT ENDPOINTS ====================

    @PutMapping("/{cardNumber}")
    @Operation(summary = "Update card", description = "Updates a card with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid card details",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "card-bad-request", value = ApiResponseCards.ERROR_400_INVALID_CARD_DETAILS))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_CARD)))
    })
    public ResponseEntity<?> updateCard(@PathVariable String cardNumber, @Valid @RequestBody Card updatedCard) {
        try {
            log.info("Updating card with number: {}", cardNumber);
            Optional<Card> updated = cardService.updateCard(cardNumber, updatedCard);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating card with number: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid card details"));
        }
    }

    // ==================== PATCH ENDPOINTS ====================

    @PatchMapping("/{cardNumber}/status")
    @Operation(summary = "Update card status", description = "Updates the status of a card. Only the 'status' field is allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'status' field is allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED),
                    @ExampleObject(name = "invalid-status", value = ApiResponseCards.ERROR_400_INVALID_STATUS)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_STATUS)))
    })
    public ResponseEntity<?> updateStatus(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'status' field is present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateStatusRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateStatusRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateStatus request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'status' field is allowed in the request body"));
            } catch (InvalidFormatException e) {
                // Check if the error is related to the status enum field
                if (e.getPath() != null && !e.getPath().isEmpty() && 
                    e.getPath().get(e.getPath().size() - 1).getFieldName() != null &&
                    e.getPath().get(e.getPath().size() - 1).getFieldName().equals("status")) {
                    log.warn("Invalid status value in updateStatus request for card: {}. Value: {}", cardNumber, e.getValue());
                    return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid status. Valid values are: ACTIVE, BLOCKED, STOLEN, LOST, TEST"));
                }
                throw e; // Re-throw if it's not a status field error
            }
            
            // Validate the request using Bean Validation
            if (request.getStatus() == null) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Status is required"));
            }
            
            log.info("Updating status for card: {} to {}", cardNumber, request.getStatus());
            Optional<Card> updated = cardService.updateStatus(cardNumber, request.getStatus());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (InvalidFormatException e) {
            // This catches InvalidFormatException that was re-thrown (for non-status field errors) or any other format errors
            log.error("Error parsing request for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (JsonProcessingException e) {
            // Catch other JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating status for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid status"));
        }
    }

    @PatchMapping("/{cardNumber}/balance")
    @Operation(summary = "Update card balance", description = "Updates the balance of a card. Only the 'balance' field is allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card balance updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'balance' field is allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_BALANCE),
                    @ExampleObject(name = "invalid-balance", value = ApiResponseCards.ERROR_400_INVALID_BALANCE)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_BALANCE)))
    })
    public ResponseEntity<?> updateBalance(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'balance' field is present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateBalanceRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateBalanceRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateBalance request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'balance' field is allowed in the request body"));
            }
            
            // Validate the request using Bean Validation
            if (request.getBalance() == null) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Balance is required"));
            }
            
            if (request.getBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Balance must be greater than or equal to 0"));
            }
            
            log.info("Updating balance for card: {} to {}", cardNumber, request.getBalance());
            Optional<Card> updated = cardService.updateBalance(cardNumber, request.getBalance());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating balance for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid balance"));
        }
    }

    @PatchMapping("/{cardNumber}/daily-limits")
    @Operation(summary = "Update daily limits", description = "Updates the daily limit amount and/or count for a card. Only the 'dailyLimitAmount' and/or 'dailyLimitCount' fields are allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily limits updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'dailyLimitAmount' and/or 'dailyLimitCount' fields are allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_DAILY_LIMITS),
                    @ExampleObject(name = "invalid-request", value = ApiResponseCards.ERROR_400_INVALID_DAILY_LIMITS)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_DAILY_LIMITS)))
    })
    public ResponseEntity<?> updateDailyLimits(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'dailyLimitAmount' and/or 'dailyLimitCount' fields are present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateDailyLimitsRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateDailyLimitsRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateDailyLimits request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'dailyLimitAmount' and/or 'dailyLimitCount' fields are allowed in the request body"));
            }
            
            // Validate the request - at least one field should be provided, and values must be valid
            if (request.getDailyLimitAmount() == null && request.getDailyLimitCount() == null) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "At least one of 'dailyLimitAmount' or 'dailyLimitCount' must be provided"));
            }
            
            if (request.getDailyLimitAmount() != null && request.getDailyLimitAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Daily limit amount must be greater than or equal to 0"));
            }
            
            if (request.getDailyLimitCount() != null && request.getDailyLimitCount() < 0) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Daily limit count must be greater than or equal to 0"));
            }
            
            log.info("Updating daily limits for card: {} - amount: {}, count: {}", cardNumber, request.getDailyLimitAmount(), request.getDailyLimitCount());
            Optional<Card> updated = cardService.updateDailyLimits(cardNumber, request.getDailyLimitAmount(), request.getDailyLimitCount());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating daily limits for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid daily limits"));
        }
    }

    @PatchMapping("/{cardNumber}/credit-limits")
    @Operation(summary = "Update credit limits", description = "Updates the credit limit and/or available credit for a card. Only the 'creditLimit' and/or 'availableCredit' fields are allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit limits updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'creditLimit' and/or 'availableCredit' fields are allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_CREDIT_LIMITS),
                    @ExampleObject(name = "invalid-request", value = ApiResponseCards.ERROR_400_INVALID_CREDIT_LIMITS)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_CREDIT_LIMITS)))
    })
    public ResponseEntity<?> updateCreditLimits(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'creditLimit' and/or 'availableCredit' fields are present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateCreditLimitsRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateCreditLimitsRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateCreditLimits request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'creditLimit' and/or 'availableCredit' fields are allowed in the request body"));
            }
            
            // Validate the request - at least one field should be provided, and values must be valid
            if (request.getCreditLimit() == null && request.getAvailableCredit() == null) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "At least one of 'creditLimit' or 'availableCredit' must be provided"));
            }
            
            if (request.getCreditLimit() != null && request.getCreditLimit().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Credit limit must be greater than or equal to 0"));
            }
            
            if (request.getAvailableCredit() != null && request.getAvailableCredit().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Available credit must be greater than or equal to 0"));
            }
            
            log.info("Updating credit limits for card: {} - limit: {}, available: {}", cardNumber, request.getCreditLimit(), request.getAvailableCredit());
            Optional<Card> updated = cardService.updateCreditLimits(cardNumber, request.getCreditLimit(), request.getAvailableCredit());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating credit limits for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid credit limits"));
        }
    }

    @PatchMapping("/{cardNumber}/avs")
    @Operation(summary = "Update AVS information", description = "Updates the AVS (Address Verification System) information for a card. Only the 'avsAddressLine1' and/or 'avsPostalCode' fields are allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AVS information updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'avsAddressLine1' and/or 'avsPostalCode' fields are allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_AVS),
                    @ExampleObject(name = "invalid-request", value = ApiResponseCards.ERROR_400_INVALID_AVS)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_AVS)))
    })
    public ResponseEntity<?> updateAvsInfo(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'avsAddressLine1' and/or 'avsPostalCode' fields are present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateAvsRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateAvsRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateAvsInfo request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'avsAddressLine1' and/or 'avsPostalCode' fields are allowed in the request body"));
            }
            
            // Validate the request - at least one field should be provided, and values must be valid
            if (request.getAvsAddressLine1() == null && request.getAvsPostalCode() == null) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "At least one of 'avsAddressLine1' or 'avsPostalCode' must be provided"));
            }
            
            if (request.getAvsAddressLine1() != null && request.getAvsAddressLine1().length() > 255) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "AVS address line 1 must not exceed 255 characters"));
            }
            
            if (request.getAvsPostalCode() != null && request.getAvsPostalCode().length() > 16) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "AVS postal code must not exceed 16 characters"));
            }
            
            log.info("Updating AVS info for card: {} - address: {}, postal: {}", cardNumber, request.getAvsAddressLine1(), request.getAvsPostalCode());
            Optional<Card> updated = cardService.updateAvsInfo(cardNumber, request.getAvsAddressLine1(), request.getAvsPostalCode());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating AVS info for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid AVS information"));
        }
    }

    @PatchMapping("/{cardNumber}/notes")
    @Operation(summary = "Update card notes", description = "Updates the notes field for a card. Only the 'notes' field is allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notes updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'notes' field is allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_NOTES),
                    @ExampleObject(name = "invalid-request", value = ApiResponseCards.ERROR_400_INVALID_NOTES)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_NOTES)))
    })
    public ResponseEntity<?> updateNotes(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'notes' field is present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateNotesRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateNotesRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateNotes request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'notes' field is allowed in the request body"));
            }
            
            // Validate the request - notes can be null but if provided, must not exceed 255 characters
            if (request.getNotes() != null && request.getNotes().length() > 255) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Notes must not exceed 255 characters"));
            }
            
            log.info("Updating notes for card: {} - notes: {}", cardNumber, request.getNotes());
            Optional<Card> updated = cardService.updateNotes(cardNumber, request.getNotes());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating notes for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid notes"));
        }
    }

    @PatchMapping("/{cardNumber}/processing-rule-reason")
    @Operation(summary = "Update processing rule reason", description = "Updates the processing rule reason for a card. Only the 'processingRuleReason' field is allowed in the request body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Processing rule reason updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "400", description = "Invalid request - only 'processingRuleReason' field is allowed",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "extra-fields", value = ApiResponseCards.ERROR_400_EXTRA_FIELDS_NOT_ALLOWED_PROCESSING_RULE),
                    @ExampleObject(name = "invalid-request", value = ApiResponseCards.ERROR_400_INVALID_PROCESSING_RULE_REASON)
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_UPDATING_PROCESSING_RULE_REASON)))
    })
    public ResponseEntity<?> updateProcessingRuleReason(@PathVariable String cardNumber, @RequestBody String requestBody) {
        try {
            // Validate that only 'processingRuleReason' field is present using ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper strictMapper = new ObjectMapper();
            strictMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            
            UpdateProcessingRuleReasonRequest request;
            try {
                request = strictMapper.readValue(requestBody, UpdateProcessingRuleReasonRequest.class);
            } catch (UnrecognizedPropertyException e) {
                log.warn("Extra fields not allowed in updateProcessingRuleReason request for card: {}. Field: {}", cardNumber, e.getPropertyName());
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Only the 'processingRuleReason' field is allowed in the request body"));
            }
            
            // Validate the request - processingRuleReason can be null but if provided, must not exceed 100 characters
            if (request.getProcessingRuleReason() != null && request.getProcessingRuleReason().length() > 100) {
                return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Processing rule reason must not exceed 100 characters"));
            }
            
            log.info("Updating processing rule reason for card: {} - reason: {}", cardNumber, request.getProcessingRuleReason());
            Optional<Card> updated = cardService.updateProcessingRuleReason(cardNumber, request.getProcessingRuleReason());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (JsonProcessingException e) {
            // Catch JSON parsing errors (like malformed JSON)
            log.error("Error parsing JSON for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid JSON format"));
        } catch (Exception e) {
            log.error("Error updating processing rule reason for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid processing rule reason"));
        }
    }

    // ==================== DELETE ENDPOINTS ====================

    @DeleteMapping("/{cardNumber}")
    @Operation(summary = "Delete card", description = "Deletes a card by its card number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card deleted successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "success", value = ApiResponseCards.SUCCESS_200_DELETE))),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = ApiResponseCards.ERROR_404_NOT_FOUND))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = ApiResponseCards.ERROR_500_DELETING_CARD)))
    })
    public ResponseEntity<?> deleteCard(@PathVariable String cardNumber) {
        try {
            log.info("Deleting card with number: {}", cardNumber);
            boolean deleted = cardService.deleteCard(cardNumber);
            if (deleted) {
                log.info("Card deleted successfully: {}", cardNumber);
                Map<String, Object> successBody = new HashMap<>();
                successBody.put("status", 200);
                successBody.put("message", "Card deleted successfully");
                successBody.put("cardNumber", cardNumber);
                return ResponseEntity.ok(successBody);
            } else {
                log.warn("Card not found with number: {}", cardNumber);
                return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
            }
        } catch (Exception e) {
            log.error("Error deleting card with number: {}", cardNumber, e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while deleting card"));
        }
    }
}
