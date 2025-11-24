package com.pay.cardpaysimulator.dto;

import com.pay.cardpaysimulator.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSearchRequest {
    @Schema(description = "BIN (Bank Identification Number)", example = "424242")
    private String bin;

    @Schema(description = "Last 4 digits of card", example = "4242")
    private String last4;

    @Schema(description = "Country code", example = "US")
    private String countryCode;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Card status", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Card brand (case-insensitive)", example = "VISA")
    private String brand;
}

