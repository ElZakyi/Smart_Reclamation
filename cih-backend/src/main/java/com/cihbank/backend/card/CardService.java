package com.cihbank.backend.card;

import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    public CardService(CardRepository cardRepository, UserRepository userRepository){
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }
    public Card createCard(Integer idUser, String cardNumberMasked, String cardType, Double currentLimit, String cvc, LocalDate expiresAt){
        User user = userRepository.findById(idUser).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable"));
        Card card = new Card();
        card.setUser(user);
        card.setCardNumberMasked(cardNumberMasked);
        card.setCardType(cardType   );
        card.setCurrentLimit(currentLimit);
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());
        card.setCvc(cvc);
        card.setExpiryDate(expiresAt);
        return cardRepository.save(card);
    }
    public List<Card> getCardsOfUser(Integer idUser){
        return cardRepository.findByUser_IdUser(idUser);
    }
    public List<Card> getCards(){
        return cardRepository.findAll();
    }
    public Card findCardById(Integer idCard){
        return cardRepository.findById(idCard).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Carte introuvable !"));
    }
    public Card updateCard(Integer idCard, Card updatedCard){
        Card card = cardRepository.findById(idCard).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Carte introuvable !"));
        card.setCardType(updatedCard.getCardType());
        card.setCurrentLimit(updatedCard.getCurrentLimit());
        card.setCardNumberMasked(updatedCard.getCardNumberMasked());
        return cardRepository.save(card);
    }
    public String deleteCard(Integer idCard){
        cardRepository.deleteById(idCard);
        return "Carte supprimée avec succés !";
    }
}
