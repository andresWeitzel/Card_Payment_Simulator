package com.pay.cardpaysimulator.config;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.CardStatus;
import com.pay.cardpaysimulator.model.ScenarioType;
import com.pay.cardpaysimulator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CardRepository cardRepository;

    @Bean
    public CommandLineRunner seedCardsOnStartup() {
        return args -> {
            long count = cardRepository.count();
            if (count > 0) {
                log.info("Skipping seed: {} cards already present", count);
                return;
            }
            log.info("Seeding initial test cards (3)...");
            List<Card> cards = List.of(
                Card.builder()
                        .cardNumber("4242424242424242")
                        .cardholderName("John Doe")
                        .expirationDate(LocalDate.now().plusYears(2))
                        .cvv("123")
                        .balance(new BigDecimal("1000.00"))
                        .processingRule(ScenarioType.APPROVAL)
                        .status(CardStatus.ACTIVE)
                        .countryCode("US")
                        .currency("USD")
                        .dailyLimitAmount(new BigDecimal("5000.00"))
                        .dailyLimitCount(10)
                        .notes("Startup seed card")
                        .processingRuleReason("Startup seed")
                        .build(),
                Card.builder()
                        .cardNumber("5555555555554444")
                        .cardholderName("Jane Smith")
                        .expirationDate(LocalDate.now().plusYears(1))
                        .cvv("456")
                        .balance(new BigDecimal("750.00"))
                        .processingRule(ScenarioType.APPROVAL)
                        .status(CardStatus.ACTIVE)
                        .countryCode("US")
                        .currency("USD")
                        .dailyLimitAmount(new BigDecimal("5000.00"))
                        .dailyLimitCount(10)
                        .notes("Startup seed card")
                        .processingRuleReason("Startup seed")
                        .build(),
                Card.builder()
                        .cardNumber("6011111111111117")
                        .cardholderName("Alice Brown")
                        .expirationDate(LocalDate.now().plusYears(3))
                        .cvv("321")
                        .balance(new BigDecimal("500.00"))
                        .processingRule(ScenarioType.APPROVAL)
                        .status(CardStatus.ACTIVE)
                        .countryCode("US")
                        .currency("USD")
                        .dailyLimitAmount(new BigDecimal("5000.00"))
                        .dailyLimitCount(10)
                        .notes("Startup seed card")
                        .processingRuleReason("Startup seed")
                        .build()
            );
            cardRepository.saveAll(cards);
            log.info("Seed completed: {} cards inserted", cards.size());
        };
    }
}
