package com.pay.cardpaysimulator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    @Schema(description = "16-digit card number", example = "1234567890123456")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    @Schema(description = "3 or 4-digit CVV code", example = "123")
    private String cvv;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Payment amount", example = "100.00")
    private BigDecimal amount;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Schema(description = "Optional payment description", example = "Payment for services")
    private String description;
} 