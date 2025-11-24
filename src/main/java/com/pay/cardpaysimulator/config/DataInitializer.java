package com.pay.cardpaysimulator.config;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.enums.CardStatus;
import com.pay.cardpaysimulator.enums.ScenarioType;
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
                            .processingRule(ScenarioType.DECLINE)
                            .status(CardStatus.BLOCKED)
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
                            .processingRule(ScenarioType.ERROR)
                            .status(CardStatus.BLOCKED)
                            .countryCode("US")
                            .currency("USD")
                            .dailyLimitAmount(new BigDecimal("5000.00"))
                            .dailyLimitCount(10)
                            .notes("Startup seed card")
                            .processingRuleReason("Startup seed")
                            .build(),
                    Card.builder()
                            .cardNumber("378282246310005")
                            .cardholderName("Maria Gonzalez")
                            .expirationDate(LocalDate.now().plusYears(4))
                            .cvv("789")
                            .balance(new BigDecimal("2500.00"))
                            .processingRule(ScenarioType.APPROVAL)
                            .status(CardStatus.TEST)
                            .countryCode("MX")
                            .currency("MXN")
                            .dailyLimitAmount(new BigDecimal("8000.00"))
                            .dailyLimitCount(15)
                            .notes("Cross-border approval testing")
                            .processingRuleReason("High balance approval")
                            .build(),
                    Card.builder()
                            .cardNumber("6011000990139424")
                            .cardholderName("Kenji Nakamura")
                            .expirationDate(LocalDate.now().plusMonths(18))
                            .cvv("159")
                            .balance(new BigDecimal("1200.00"))
                            .processingRule(ScenarioType.DECLINE)
                            .status(CardStatus.LOST)
                            .countryCode("JP")
                            .currency("JPY")
                            .dailyLimitAmount(new BigDecimal("300000.00"))
                            .dailyLimitCount(6)
                            .notes("Lost card decline scenario")
                            .processingRuleReason("Lost card flag")
                            .build(),
                    Card.builder()
                            .cardNumber("5200828282828210")
                            .cardholderName("Olivia Martin")
                            .expirationDate(LocalDate.now().plusYears(5))
                            .cvv("842")
                            .balance(new BigDecimal("150.00"))
                            .processingRule(ScenarioType.ERROR)
                            .status(CardStatus.STOLEN)
                            .countryCode("CA")
                            .currency("CAD")
                            .dailyLimitAmount(new BigDecimal("1000.00"))
                            .dailyLimitCount(3)
                            .notes("Simulate processor error flows")
                            .processingRuleReason("Fraud signals trigger error")
                            .build(),
                    Card.builder()
                            .cardNumber("4000056655665556")
                            .cardholderName("George Campbell")
                            .expirationDate(LocalDate.now().plusYears(2))
                            .cvv("618")
                            .balance(new BigDecimal("50.00"))
                            .processingRule(ScenarioType.APPROVAL)
                            .status(CardStatus.BLOCKED)
                            .countryCode("GB")
                            .currency("GBP")
                            .dailyLimitAmount(new BigDecimal("200.00"))
                            .dailyLimitCount(2)
                            .notes("Manual block but allow approvals")
                            .processingRuleReason("Ops override testing")
                            .build(),
                    Card.builder()
                            .cardNumber("5105105105105100")
                            .cardholderName("Lucas Pereira")
                            .expirationDate(LocalDate.now().plusYears(1))
                            .cvv("274")
                            .balance(new BigDecimal("980.00"))
                            .processingRule(ScenarioType.DECLINE)
                            .status(CardStatus.ACTIVE)
                            .countryCode("BR")
                            .currency("BRL")
                            .dailyLimitAmount(new BigDecimal("3000.00"))
                            .dailyLimitCount(8)
                            .notes("Active card with rule-based decline")
                            .processingRuleReason("Velocity decline scenario")
                            .build()
            );
            log.info("Seeding initial test cards ({})...", cards.size());
            cardRepository.saveAll(cards);
            log.info("Seed completed: {} cards inserted", cards.size());
        };
    }
}
