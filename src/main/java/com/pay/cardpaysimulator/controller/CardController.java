package com.pay.cardpaysimulator.controller;

import com.pay.cardpaysimulator.constants.ApiResponseCards;
import com.pay.cardpaysimulator.dto.*;
import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.CardBrand;
import com.pay.cardpaysimulator.model.CardStatus;
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

    @PutMapping("/{cardNumber}")
    @Operation(summary = "Update card", description = "Updates a card with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid card details",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "card-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid card details\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating card\"\n}")))
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

    @PatchMapping("/{cardNumber}/status")
    @Operation(summary = "Update card status", description = "Updates the status of a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid status",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "status-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid status\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating status\"\n}")))
    })
    public ResponseEntity<?> updateStatus(@PathVariable String cardNumber, @Valid @RequestBody UpdateStatusRequest request) {
        try {
            log.info("Updating status for card: {} to {}", cardNumber, request.getStatus());
            Optional<Card> updated = cardService.updateStatus(cardNumber, request.getStatus());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating status for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid status"));
        }
    }

    @PatchMapping("/{cardNumber}/balance")
    @Operation(summary = "Update card balance", description = "Updates the balance of a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card balance updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid balance",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "balance-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid balance\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating balance\"\n}")))
    })
    public ResponseEntity<?> updateBalance(@PathVariable String cardNumber, @Valid @RequestBody UpdateBalanceRequest request) {
        try {
            log.info("Updating balance for card: {} to {}", cardNumber, request.getBalance());
            Optional<Card> updated = cardService.updateBalance(cardNumber, request.getBalance());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating balance for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid balance"));
        }
    }

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

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get cards by status", description = "Retrieves all cards with the specified status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "status-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid status\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by status\"\n}")))
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
                examples = @ExampleObject(name = "brand-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid brand\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by brand\"\n}")))
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

    @PostMapping("/{cardNumber}/block")
    @Operation(summary = "Block card", description = "Blocks a card by setting its status to BLOCKED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card blocked successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while blocking card\"\n}")))
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
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while unblocking card\"\n}")))
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

    // Search endpoints
    @GetMapping("/by-bin/{bin}")
    @Operation(summary = "Get cards by BIN", description = "Retrieves all cards with the specified BIN (Bank Identification Number)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "cards-found", value = "[\n  {\n    \"id\": 1,\n    \"cardNumber\": \"4242424242424242\",\n    \"bin\": \"424242\"\n  }\n]"),
                    @ExampleObject(name = "no-cards", value = "{\n  \"status\": 200,\n  \"message\": \"No cards found with BIN: 424242\"\n}")
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by BIN\"\n}")))
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
                    @ExampleObject(name = "cards-found", value = "[\n  {\n    \"id\": 1,\n    \"cardNumber\": \"4242424242424242\",\n    \"last4\": \"4242\"\n  }\n]"),
                    @ExampleObject(name = "no-cards", value = "{\n  \"status\": 200,\n  \"message\": \"No cards found with last4: 4242\"\n}")
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by last4\"\n}")))
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
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by country\"\n}")))
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
                    @ExampleObject(name = "cards-found", value = "[\n  {\n    \"id\": 1,\n    \"cardNumber\": \"4242424242424242\",\n    \"currency\": \"USD\"\n  }\n]"),
                    @ExampleObject(name = "no-cards", value = "{\n  \"status\": 200,\n  \"message\": \"No cards found with currency: USD\"\n}")
                })),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by currency\"\n}")))
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

    @PostMapping("/search")
    @Operation(summary = "Search cards with multiple filters", description = "Searches for cards using multiple optional filters (bin, last4, countryCode, currency, status, brand). All string filters are case-insensitive.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid brand in search request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid brand\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while searching cards\"\n}")))
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

    // Update endpoints for specific fields
    @PatchMapping("/{cardNumber}/daily-limits")
    @Operation(summary = "Update daily limits", description = "Updates the daily limit amount and/or count for a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily limits updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid daily limits\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating daily limits\"\n}")))
    })
    public ResponseEntity<?> updateDailyLimits(@PathVariable String cardNumber, @Valid @RequestBody UpdateDailyLimitsRequest request) {
        try {
            log.info("Updating daily limits for card: {} - amount: {}, count: {}", cardNumber, request.getDailyLimitAmount(), request.getDailyLimitCount());
            Optional<Card> updated = cardService.updateDailyLimits(cardNumber, request.getDailyLimitAmount(), request.getDailyLimitCount());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating daily limits for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid daily limits"));
        }
    }

    @PatchMapping("/{cardNumber}/credit-limits")
    @Operation(summary = "Update credit limits", description = "Updates the credit limit and/or available credit for a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit limits updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid credit limits\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating credit limits\"\n}")))
    })
    public ResponseEntity<?> updateCreditLimits(@PathVariable String cardNumber, @Valid @RequestBody UpdateCreditLimitsRequest request) {
        try {
            log.info("Updating credit limits for card: {} - limit: {}, available: {}", cardNumber, request.getCreditLimit(), request.getAvailableCredit());
            Optional<Card> updated = cardService.updateCreditLimits(cardNumber, request.getCreditLimit(), request.getAvailableCredit());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating credit limits for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid credit limits"));
        }
    }

    @PatchMapping("/{cardNumber}/avs")
    @Operation(summary = "Update AVS information", description = "Updates the AVS (Address Verification System) information for a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AVS information updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid AVS information\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating AVS information\"\n}")))
    })
    public ResponseEntity<?> updateAvsInfo(@PathVariable String cardNumber, @Valid @RequestBody UpdateAvsRequest request) {
        try {
            log.info("Updating AVS info for card: {} - address: {}, postal: {}", cardNumber, request.getAvsAddressLine1(), request.getAvsPostalCode());
            Optional<Card> updated = cardService.updateAvsInfo(cardNumber, request.getAvsAddressLine1(), request.getAvsPostalCode());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating AVS info for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid AVS information"));
        }
    }

    @PatchMapping("/{cardNumber}/notes")
    @Operation(summary = "Update card notes", description = "Updates the notes field for a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notes updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid notes\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating notes\"\n}")))
    })
    public ResponseEntity<?> updateNotes(@PathVariable String cardNumber, @Valid @RequestBody UpdateNotesRequest request) {
        try {
            log.info("Updating notes for card: {} - notes: {}", cardNumber, request.getNotes());
            Optional<Card> updated = cardService.updateNotes(cardNumber, request.getNotes());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating notes for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid notes"));
        }
    }

    @PatchMapping("/{cardNumber}/processing-rule-reason")
    @Operation(summary = "Update processing rule reason", description = "Updates the processing rule reason for a card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Processing rule reason updated successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid processing rule reason\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating processing rule reason\"\n}")))
    })
    public ResponseEntity<?> updateProcessingRuleReason(@PathVariable String cardNumber, @Valid @RequestBody UpdateProcessingRuleReasonRequest request) {
        try {
            log.info("Updating processing rule reason for card: {} - reason: {}", cardNumber, request.getProcessingRuleReason());
            Optional<Card> updated = cardService.updateProcessingRuleReason(cardNumber, request.getProcessingRuleReason());
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(404).body(errorBody(404, "Not Found", "Card not found"));
        } catch (Exception e) {
            log.error("Error updating processing rule reason for card: {}", cardNumber, e);
            return ResponseEntity.badRequest().body(errorBody(400, "Bad Request", "Invalid processing rule reason"));
        }
    }

    // CVV attempts management
    @PostMapping("/{cardNumber}/increment-cvv-attempts")
    @Operation(summary = "Increment failed CVV attempts", description = "Increments the failed CVV attempts counter and updates the last failed attempt timestamp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Failed CVV attempts incremented successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while incrementing CVV attempts\"\n}")))
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
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while resetting CVV attempts\"\n}")))
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
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while updating last used timestamp\"\n}")))
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
} 