package com.pay.cardpaysimulator.controller;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.service.CardTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cards/test")
@RequiredArgsConstructor
@Tag(name = "Card Management Test", description = "Endpoints for managing test card scenarios")
public class CardTestController {

    private final CardTestService cardTestService;

    private Map<String, Object> errorBody(int status, String error, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("error", error);
        m.put("message", message);
        return m;
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize test scenario cards", description = "Creates a set of official test cards with different scenarios (approval, decline, error)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test scenario cards initialized successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "initialize-server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while initializing test cards\"\n}")))
    })
    public ResponseEntity<?> initializeTestScenarioCards() {
        try {
            List<Card> savedCards = cardTestService.initializeTestScenarios();
            return ResponseEntity.ok(savedCards);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while initializing test cards"));
        }
    }

    @GetMapping("/cards")
    @Operation(summary = "Get test cards scenarios information", description = "Retrieves information about available test card scenarios aligned with initialization")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test scenarios information retrieved successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "scenarios-example", value = "{\n  \"approval\": [\n    { \"card_number\": \"4242424242424242\", \"cvv\": \"123\", \"processing_rule\": \"APPROVAL\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Standard approval test card\", \"processing_rule_reason\": \"Standard approval test card\" },\n    { \"card_number\": \"5555555555554444\", \"cvv\": \"456\", \"processing_rule\": \"APPROVAL\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Standard approval test card\", \"processing_rule_reason\": \"Standard approval test card\" }\n  ],\n  \"decline\": [\n    { \"card_number\": \"4000000000000002\", \"cvv\": \"789\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Forced decline test card\", \"processing_rule_reason\": \"Forced decline test card\" },\n    { \"card_number\": \"4000000000000010\", \"cvv\": \"321\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Forced decline test card\", \"processing_rule_reason\": \"Forced decline test card\" },\n    { \"card_number\": \"4000000000009995\", \"cvv\": \"123\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Low funds test card\", \"processing_rule_reason\": \"Low funds test card\" },\n    { \"card_number\": \"4000000000009987\", \"cvv\": \"456\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Low funds test card\", \"processing_rule_reason\": \"Low funds test card\" },\n    { \"card_number\": \"4000000000000069\", \"cvv\": \"789\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Expired card test\", \"processing_rule_reason\": \"Expired card test\" },\n    { \"card_number\": \"4000000000000127\", \"cvv\": \"321\", \"processing_rule\": \"DECLINE\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Expired card test\", \"processing_rule_reason\": \"Expired card test\" }\n  ],\n  \"error\": [\n    { \"card_number\": \"4000000000000341\", \"cvv\": \"456\", \"processing_rule\": \"ERROR\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Simulated processing error\", \"processing_rule_reason\": \"Simulated processing error\" },\n    { \"card_number\": \"4000000000000119\", \"cvv\": \"789\", \"processing_rule\": \"ERROR\", \"status\": \"TEST\", \"country_code\": \"US\", \"currency\": \"USD\", \"daily_limit_amount\": \"5000.00\", \"daily_limit_count\": \"10\", \"notes\": \"Simulated processing error\", \"processing_rule_reason\": \"Simulated processing error\" }\n  ]\n}"))),
        @ApiResponse(responseCode = "409", description = "Test cards not initialized",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "not-initialized", value = "{\n  \"status\": 409,\n  \"error\": \"Conflict\",\n  \"message\": \"Test cards not initialized. Run POST /api/cards/test/initialize first.\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "server-error", value = "{\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error while fetching scenarios\"\n}")))
    })
    public ResponseEntity<?> getTestScenarios() {
        try {
            if (!cardTestService.isTestScenariosInitialized()) {
                Map<String, Object> body = errorBody(409, "Conflict", "Test cards not initialized. Run POST /api/cards/test/initialize first.");
                return ResponseEntity.status(409).body(body);
            }
            Map<String, List<Map<String, String>>> scenarios = cardTestService.getTestScenariosInfo();
            return ResponseEntity.ok(scenarios);
        } catch (Exception e) {
            log.error("Error fetching test scenarios information", e);
            return ResponseEntity.status(500).body(errorBody(500, "Internal Server Error", "Unexpected error while fetching scenarios"));
        }
    }
}


