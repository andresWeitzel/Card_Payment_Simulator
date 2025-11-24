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
public class UpdateAvsRequest {
    @Size(max = 255, message = "AVS address line 1 must not exceed 255 characters")
    @Schema(description = "AVS address line 1", example = "123 Main Street")
    private String avsAddressLine1;

    @Size(max = 16, message = "AVS postal code must not exceed 16 characters")
    @Schema(description = "AVS postal code", example = "12345")
    private String avsPostalCode;
}

