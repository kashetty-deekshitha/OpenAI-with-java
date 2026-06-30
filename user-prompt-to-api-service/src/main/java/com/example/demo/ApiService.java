package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Object callApi(ApiRequest request) {


        String baseUrl = "http://localhost:8081";
        String url = baseUrl + request.getEndpoint();

        if ("GET".equalsIgnoreCase(request.getMethod())) {

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

            request.getParams().forEach(builder::queryParam);

            return restTemplate.getForObject(builder.toUriString(), Object.class);
        }

        throw new RuntimeException("Unsupported method");
    }
}

