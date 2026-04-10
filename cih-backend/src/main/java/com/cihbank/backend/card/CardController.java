package com.cihbank.backend.card;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;
    public CardController(CardService cardService){
        this.cardService = cardService;
    }
    @GetMapping
    public List<Card> getAllCard(){
        return cardService.getCards();
    }
    @GetMapping("/user/{idUser}")
    public List<Card> getCardsOfUser(@PathVariable Integer idUser){
        return cardService.getCardsOfUser(idUser);
    }
    @GetMapping("/{idCard}")
    public Card getCardById(@PathVariable Integer idCard){
        return cardService.findCardById(idCard);
    }
    @PostMapping("/{idUser}")
    public Card createCard(@PathVariable Integer idUser, @RequestBody Card card){
        String cardNumber = card.getCardNumberMasked();
        String cardType = card.getCardType();
        Double currentLimit = card.getCurrentLimit();
        String cvc = card.getCvc();
        LocalDate expiresAt = card.getExpiryDate();
        return cardService.createCard(idUser,cardNumber,cardType,currentLimit,cvc,expiresAt);
    }
    @PutMapping("/{idCard}")
    public Card updateCard(@PathVariable Integer idCard,@RequestBody Card card){
        return cardService.updateCard(idCard,card);
    }
    @DeleteMapping("/{idCard}")
    public String deleteCard(@PathVariable Integer idCard){
        return cardService.deleteCard(idCard);
    }
}
