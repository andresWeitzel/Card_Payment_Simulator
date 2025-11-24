package com.pay.cardpaysimulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateProcessingRuleReasonRequest {
    @Size(max = 100, message = "Processing rule reason must not exceed 100 characters")
    @Schema(description = "Processing rule reason", example = "Startup seed")
    private String processingRuleReason;
}

