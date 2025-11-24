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
public class UpdateNotesRequest {
    @Size(max = 255, message = "Notes must not exceed 255 characters")
    @Schema(description = "Card notes", example = "Startup seed card")
    private String notes;
}

