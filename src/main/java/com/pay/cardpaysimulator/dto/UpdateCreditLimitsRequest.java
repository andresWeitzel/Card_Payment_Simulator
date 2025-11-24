package com.pay.cardpaysimulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateCreditLimitsRequest {
    @DecimalMin(value = "0.0", message = "Credit limit must be greater than or equal to 0")
    @Schema(description = "Credit limit", example = "10000.00")
    private BigDecimal creditLimit;

    @DecimalMin(value = "0.0", message = "Available credit must be greater than or equal to 0")
    @Schema(description = "Available credit", example = "7500.00")
    private BigDecimal availableCredit;
}

