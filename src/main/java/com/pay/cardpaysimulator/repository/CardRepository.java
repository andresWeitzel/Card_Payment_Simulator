package com.pay.cardpaysimulator.repository;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.CardBrand;
import com.pay.cardpaysimulator.model.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByStatus(CardStatus status);
    List<Card> findByBrand(CardBrand brand);
} 