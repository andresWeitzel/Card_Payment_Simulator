package com.pay.cardpaysimulator.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pay.cardpaysimulator.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    @Schema(description = "New card status", example = "BLOCKED")
    private CardStatus status;
}

