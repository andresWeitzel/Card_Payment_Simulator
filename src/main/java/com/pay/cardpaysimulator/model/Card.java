package com.pay.cardpaysimulator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})$", 
             message = "Invalid card number format")
    @Column(unique = true, nullable = false)
    private String cardNumber;

    @NotBlank(message = "Cardholder name is required")
    @Column(nullable = false)
    private String cardholderName;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Card must not be expired")
    @Column(nullable = false)
    private LocalDate expirationDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    @Column(nullable = false)
    private String cvv;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater than or equal to 0")
    @Column(nullable = false)
    private BigDecimal balance;
} 