package com.pay.cardpaysimulator.dto;

import com.pay.cardpaysimulator.model.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    @Schema(description = "New card status", example = "BLOCKED")
    private CardStatus status;
}

