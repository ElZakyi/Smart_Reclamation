package com.cihbank.backend.otp;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SMSService {

    private final String API_KEY = "ca9792e2960bbf6ab1621ae83250485c-2b9bae60-660e-4c77-b2e7-5f266cf6bd19";
    private final String BASE_URL = "https://2ym84w.api.infobip.com";

    public void sendSms(String to, String message) {

        // ✅ 1. Validation
        if (to == null || to.isEmpty()) {
            throw new RuntimeException("Numéro de téléphone invalide");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = BASE_URL + "/sms/2/text/advanced";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "App " + API_KEY);

            Map<String, Object> body = Map.of(
                    "messages", new Object[]{
                            Map.of(
                                    "from", "CIHBank",
                                    "destinations", new Object[]{
                                            Map.of("to", to)
                                    },
                                    "text", message
                            )
                    }
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // ✅ 2. récupérer la réponse
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur API SMS: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Erreur envoi SMS: " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer le SMS");
        }
    }
}