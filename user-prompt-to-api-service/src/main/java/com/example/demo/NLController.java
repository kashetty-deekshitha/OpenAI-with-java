package com.example.demo;

import com.example.demo.NLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nl")
public class NLController {

    @Autowired
    private NLService nlService;

    @PostMapping("/ask")
    public ResponseEntity<?> handleQuery(@RequestBody String query) {
        Object response = nlService.processQuery(query);
        return ResponseEntity.ok(response);
    }
}