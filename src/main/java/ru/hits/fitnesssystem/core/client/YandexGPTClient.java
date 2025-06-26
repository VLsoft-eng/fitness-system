package ru.hits.fitnesssystem.core.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.hits.fitnesssystem.core.exception.BadRequestException;

import java.util.concurrent.CompletableFuture;

@Component
public class YandexGPTClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String folderId;
    private final String apiUrl;
    private final String modelUri;

    public YandexGPTClient(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           @Value("${yandex.gpt.api-key}") String apiKey,
                           @Value("${yandex.gpt.folder-id}") String folderId,
                           @Value("${yandex.gpt.api-url:https://llm.api.cloud.yandex.net/foundationModels/v1/completion}") String apiUrl,
                           @Value("${yandex.gpt.model-uri:gpt://<идентификатор_каталога>/yandexgpt}") String modelUri) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.folderId = folderId;
        this.apiUrl = apiUrl;
        this.modelUri = modelUri.replace("${folder-id}", folderId);
        if (folderId == null || folderId.isBlank()) {
            throw new IllegalArgumentException("Yandex GPT folder-id cannot be empty");
        }
    }

    public CompletableFuture<String> sendRequest(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("x-folder-id", folderId);

        String requestBody = String.format("""
                {
                    "modelUri": "%s",
                    "completionOptions": {
                        "stream": false,
                        "temperature": 0.6,
                        "maxTokens": 500
                    },
                    "messages": [
                        {
                            "role": "user",
                            "text": "%s"
                        },
                        {
                            "role": "system",
                            "text": "Представь, что ты АИ-фитнес тренер. Без обрамления, дай сухой совет по следующему запросу ниже."
                        }
             
                    ]
                }
                """, modelUri, prompt.replace("\"", "\\\""));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = restTemplate.postForObject(apiUrl, request, String.class);
                return extractAdviceFromResponse(response);
            } catch (Exception e) {
                throw new BadRequestException("Failed to get response from YandexGPT: " + e.getMessage());
            }
        });
    }

    private String extractAdviceFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode result = jsonNode.path("result");
            JsonNode alternatives = result.path("alternatives");
            if (alternatives.isArray() && alternatives.size() > 0) {
                return alternatives.get(0).path("message").path("text").asText();
            } else {
                throw new BadRequestException("No alternatives found in YandexGPT response");
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse YandexGPT response: " + e.getMessage());
        }
    }
}