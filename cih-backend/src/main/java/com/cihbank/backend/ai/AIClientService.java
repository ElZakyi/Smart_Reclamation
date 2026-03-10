package com.cihbank.backend.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIClientService {
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    private RestTemplate restTemplate = new RestTemplate();
    public Map<String, Object> classifyDescription(String description){
        try{
            String url = aiServiceUrl + "/classify";
            Map<String, Object> body = new HashMap<>();
            body.put("description",description);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> request = new HttpEntity<>(body,headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url,request, Map.class);
            return response.getBody();
        }catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "IA service indisponible: " + ex.getMessage());
        }
    }
}
