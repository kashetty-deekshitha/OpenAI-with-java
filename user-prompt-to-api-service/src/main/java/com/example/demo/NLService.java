package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NLService {

    @Autowired
    private LLMService llmService;

    @Autowired
    private ApiService apiService;

    public Object processQuery(String query) {

        // Step 1: Convert NL → API request
        ApiRequest apiRequest = llmService.translateToApi(query);

        // Step 2: Call actual API
        return apiService.callApi(apiRequest);
    }
}