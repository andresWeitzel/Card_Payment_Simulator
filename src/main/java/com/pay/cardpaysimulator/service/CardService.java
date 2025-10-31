package com.pay.cardpaysimulator.service;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.CardBrand;
import com.pay.cardpaysimulator.model.CardStatus;
import com.pay.cardpaysimulator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    @Transactional
    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Card> getByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    @Transactional
    public Optional<Card> updateCard(String cardNumber, Card updatedCard) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(existingCard -> {
                    updatedCard.setId(existingCard.getId());
                    return cardRepository.save(updatedCard);
                });
    }

    @Transactional
    public Optional<Card> updateStatus(String cardNumber, CardStatus status) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setStatus(status);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateBalance(String cardNumber, BigDecimal balance) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setBalance(balance);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public boolean deleteCard(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    cardRepository.delete(card);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Card> getByStatus(CardStatus status) {
        return cardRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Card> getByBrand(CardBrand brand) {
        return cardRepository.findByBrand(brand);
    }

    @Transactional
    public Optional<Card> blockCard(String cardNumber) {
        return updateStatus(cardNumber, CardStatus.BLOCKED);
    }

    @Transactional
    public Optional<Card> unblockCard(String cardNumber) {
        return updateStatus(cardNumber, CardStatus.ACTIVE);
    }
}


