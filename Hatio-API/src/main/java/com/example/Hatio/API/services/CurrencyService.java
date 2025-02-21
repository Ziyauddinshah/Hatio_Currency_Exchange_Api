package com.example.Hatio.API.services;
import com.example.Hatio.API.dal.ExchangeRateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    private final ExchangeRateClient exchangeRateClient;

    @Autowired
    public CurrencyService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public List<Object> exchangeRatesOfBaseCurrency(String baseCurrency){
        return exchangeRateClient.exchangeRatesOfBaseCurrency(baseCurrency);
    }
    public double convertCurrency(String from, String to, double amount) {
        double rate = exchangeRateClient.postCurrencyExchangeRate(from, to);
        return amount * rate;
    }
}