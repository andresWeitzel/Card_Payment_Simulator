package com.pay.cardpaysimulator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateDailyLimitsRequest {
    @DecimalMin(value = "0.0", message = "Daily limit amount must be greater than or equal to 0")
    @Schema(description = "Daily limit amount", example = "5000.00")
    private BigDecimal dailyLimitAmount;

    @Min(value = 0, message = "Daily limit count must be greater than or equal to 0")
    @Schema(description = "Daily limit count", example = "10")
    private Integer dailyLimitCount;
}

