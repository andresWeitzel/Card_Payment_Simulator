package com.pay.cardpaysimulator.service;

import com.pay.cardpaysimulator.model.Card;
import com.pay.cardpaysimulator.model.CardBrand;
import com.pay.cardpaysimulator.model.CardStatus;
import com.pay.cardpaysimulator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // Search methods
    @Transactional(readOnly = true)
    public List<Card> getByBin(String bin) {
        return cardRepository.findByBin(bin);
    }

    @Transactional(readOnly = true)
    public List<Card> getByLast4(String last4) {
        return cardRepository.findByLast4(last4);
    }

    @Transactional(readOnly = true)
    public List<Card> getByCountryCode(String countryCode) {
        return cardRepository.findByCountryCodeIgnoreCase(countryCode);
    }

    @Transactional(readOnly = true)
    public List<Card> getByCurrency(String currency) {
        return cardRepository.findByCurrencyIgnoreCase(currency);
    }

    @Transactional(readOnly = true)
    public List<Card> searchCards(String bin, String last4, String countryCode, String currency, 
                                   CardStatus status, CardBrand brand) {
        Specification<Card> spec = Specification.where(null);
        
        if (bin != null && !bin.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("bin"), bin));
        }
        if (last4 != null && !last4.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("last4"), last4));
        }
        if (countryCode != null && !countryCode.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.upper(root.get("countryCode")), countryCode.toUpperCase()));
        }
        if (currency != null && !currency.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.upper(root.get("currency")), currency.toUpperCase()));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (brand != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand"), brand));
        }
        
        return cardRepository.findAll(spec);
    }

    // Update methods for specific fields
    @Transactional
    public Optional<Card> updateDailyLimits(String cardNumber, BigDecimal dailyLimitAmount, Integer dailyLimitCount) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    if (dailyLimitAmount != null) {
                        card.setDailyLimitAmount(dailyLimitAmount);
                    }
                    if (dailyLimitCount != null) {
                        card.setDailyLimitCount(dailyLimitCount);
                    }
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateCreditLimits(String cardNumber, BigDecimal creditLimit, BigDecimal availableCredit) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    if (creditLimit != null) {
                        card.setCreditLimit(creditLimit);
                    }
                    if (availableCredit != null) {
                        card.setAvailableCredit(availableCredit);
                    }
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateAvsInfo(String cardNumber, String avsAddressLine1, String avsPostalCode) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    if (avsAddressLine1 != null) {
                        card.setAvsAddressLine1(avsAddressLine1);
                    }
                    if (avsPostalCode != null) {
                        card.setAvsPostalCode(avsPostalCode);
                    }
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateNotes(String cardNumber, String notes) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setNotes(notes);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateProcessingRuleReason(String cardNumber, String processingRuleReason) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setProcessingRuleReason(processingRuleReason);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> incrementFailedCvvAttempts(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setFailedCvvAttempts(card.getFailedCvvAttempts() + 1);
                    card.setLastFailedAttemptAt(LocalDateTime.now());
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> resetFailedCvvAttempts(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setFailedCvvAttempts(0);
                    card.setLastFailedAttemptAt(null);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateLastUsedAt(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(card -> {
                    card.setLastUsedAt(LocalDateTime.now());
                    return cardRepository.save(card);
                });
    }
}


