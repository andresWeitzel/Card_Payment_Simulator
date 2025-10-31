package com.pay.cardpaysimulator.controller;

import com.pay.cardpaysimulator.dto.UpdateBalanceRequest;
import com.pay.cardpaysimulator.dto.UpdateStatusRequest;
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
                examples = @ExampleObject(name = "card-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid card details\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while creating card\"\n}")))
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
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards\"\n}")))
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
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching card\"\n}")))
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
        @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-found", value = "{\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Card not found\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while deleting card\"\n}")))
    })
    public ResponseEntity<?> deleteCard(@PathVariable String cardNumber) {
        try {
            log.info("Deleting card with number: {}", cardNumber);
            boolean deleted = cardService.deleteCard(cardNumber);
            if (deleted) {
                log.info("Card deleted successfully: {}", cardNumber);
                return ResponseEntity.ok().build();
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
    @Operation(summary = "Get cards by brand", description = "Retrieves all cards with the specified brand")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid brand",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "brand-bad-request", value = "{\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid brand\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching cards by brand\"\n}")))
    })
    public ResponseEntity<?> getCardsByBrand(@PathVariable CardBrand brand) {
        try {
            log.info("Fetching cards with brand: {}", brand);
            List<Card> cards = cardService.getByBrand(brand);
            log.info("Found {} cards with brand: {}", cards.size(), brand);
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
} 