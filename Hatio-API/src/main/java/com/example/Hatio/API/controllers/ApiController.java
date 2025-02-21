package com.example.Hatio.API.controllers;

import com.example.Hatio.API.entity.RequestEntity;
import com.example.Hatio.API.entity.ResponseEntity;
import com.example.Hatio.API.services.CurrencyService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private CurrencyService currencyService;
    @GetMapping("/rates")
    public List<Object> getCurrencyExchangeData(@RequestParam(defaultValue = "USD") String base) {
        return currencyService.exchangeRatesOfBaseCurrency(base);
    }

    @PostMapping("/convert")
    public List<Object> postCurrencyExchangeData(@RequestBody RequestEntity requestEntity) {
        String from = requestEntity.getFrom();
        String to = requestEntity.getTo();
        double amount = requestEntity.getAmount();
        double convertedAmount = currencyService.convertCurrency(from,to,amount);
        if(convertedAmount<0) {
            String message = "API is unavailable.";
            return new ArrayList<>(){{add(message);}};
        }
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setFrom(from);
        responseEntity.setTo(to);
        responseEntity.setAmount(amount);
        responseEntity.setConvertedAmount(convertedAmount);
        return Arrays.asList(responseEntity);
    }

}