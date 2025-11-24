package com.pay.cardpaysimulator.repository;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.enums.CardBrand;
import com.pay.cardpaysimulator.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByStatus(CardStatus status);
    List<Card> findByBrand(CardBrand brand);
    List<Card> findByBin(String bin);
    List<Card> findByLast4(String last4);
    List<Card> findByCountryCodeIgnoreCase(String countryCode);
    List<Card> findByCurrencyIgnoreCase(String currency);
    List<Card> findByBinAndLast4(String bin, String last4);
    List<Card> findByCountryCodeIgnoreCaseAndCurrencyIgnoreCase(String countryCode, String currency);
} 