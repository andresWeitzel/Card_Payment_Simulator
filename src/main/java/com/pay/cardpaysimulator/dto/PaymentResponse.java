package com.pay.cardpaysimulator.dto;

import com.pay.cardpaysimulator.model.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    @Schema(description = "Transaction status", example = "APPROVED")
    private TransactionStatus status;

    @Schema(description = "Response message", example = "Payment processed successfully")
    private String message;

    @Schema(description = "Transaction ID", example = "1")
    private Long transactionId;

    @Schema(description = "Transaction timestamp", example = "2024-03-20T10:30:00")
    private LocalDateTime timestamp;
} 