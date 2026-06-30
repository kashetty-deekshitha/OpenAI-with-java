package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class LLMService {

    private static final Logger log = LoggerFactory.getLogger(LLMService.class);

    public ApiRequest translateToApi(String query) {

        String prompt = """
            You are an API generator.
            
            Convert the user query into STRICT JSON format.
            
            Allowed endpoints ONLY:
            1. GET /transactions/last5 (param: userId)
            2. GET /transactions/highest (param: userId)
            
            Rules:
            - DO NOT generate any endpoint other than above
            - DO NOT use /users
            - ALWAYS include userId as "123"
            - Output ONLY JSON (no explanation)
            
            Example:
            {
              "endpoint": "/transactions/highest",
              "method": "GET",
              "params": {
                "userId": "123"
              }
            }
            
            User query:
            """ + query;

        // Call LLM (pseudo code)
        String response = callLLM(prompt);
        System.out.println(response);
        // Convert JSON string → Java Object
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response, ApiRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM response");
        }
    }

    private String callLLM(String prompt) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("YOUR_API_KEY");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
    {
      "model": "gpt-4o-mini",
      "messages": [
        {
          "role": "user",
          "content": "%s"
        }
      ]
    }
    """.formatted(prompt);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        System.out.println("RAW OpenAI Response: " + response);

        // ⚠️ You still need to extract content from JSON response
        return extractContent(response);
    }

    private String extractContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract content from OpenAI response", e);
        }
    }
}