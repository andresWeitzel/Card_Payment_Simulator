package com.pay.cardpaysimulator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScenarioType processingRule = ScenarioType.APPROVAL;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CardStatus status = CardStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private CardBrand brand;

    @Size(min = 6, max = 6)
    @Column(length = 6)
    private String bin;

    @Size(min = 4, max = 4)
    @Column(length = 4)
    private String last4;

    private Integer expirationMonth;

    private Integer expirationYear;

    @Size(max = 2)
    private String countryCode;

    @Size(max = 3)
    private String currency;

    private BigDecimal creditLimit;

    private BigDecimal availableCredit;

    private BigDecimal dailyLimitAmount;

    private Integer dailyLimitCount;

    @Builder.Default
    private Integer failedCvvAttempts = 0;

    private LocalDateTime lastFailedAttemptAt;

    private LocalDateTime lastUsedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String avsAddressLine1;

    @Size(max = 16)
    private String avsPostalCode;

    @Size(max = 255)
    private String notes;

    @Size(max = 100)
    private String processingRuleReason;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        deriveBrandAndParts();
        deriveExpirationFields();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        deriveBrandAndParts();
        deriveExpirationFields();
    }

    private void deriveBrandAndParts() {
        if (this.cardNumber != null) {
            String digits = this.cardNumber.replaceAll("\\s+", "");
            if (digits.length() >= 6) {
                this.bin = digits.substring(0, 6);
            }
            if (digits.length() >= 4) {
                this.last4 = digits.substring(digits.length() - 4);
            }
            if (digits.startsWith("4")) this.brand = CardBrand.VISA;
            else if (digits.matches("5[1-5].*")) this.brand = CardBrand.MASTERCARD;
            else if (digits.matches("3[47].*")) this.brand = CardBrand.AMEX;
            else if (digits.startsWith("6011") || digits.matches("65.*")) this.brand = CardBrand.DISCOVER;
        }
    }

    private void deriveExpirationFields() {
        if (this.expirationDate != null) {
            this.expirationMonth = this.expirationDate.getMonthValue();
            this.expirationYear = this.expirationDate.getYear();
        } else if (this.expirationMonth != null && this.expirationYear != null) {
            try {
                this.expirationDate = LocalDate.of(this.expirationYear, this.expirationMonth, 1);
            } catch (Exception ignored) {}
        }
    }
} 