package com.example.Hatio.API.services;
import com.example.Hatio.API.dal.HatioApiDataAccessLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HatioApiService {
    private final HatioApiDataAccessLayer hatioApiDataAccessLayer;

    @Autowired
    public HatioApiService(HatioApiDataAccessLayer hatioApiDataAccessLayer) {
        this.hatioApiDataAccessLayer = hatioApiDataAccessLayer;
    }

    public List<Object> ratesOfBaseCurrency(String baseCurrency){
        List<Object> result = new ArrayList<>();
        try {
            result = hatioApiDataAccessLayer.getExchangeRatesOfBaseCurrency(baseCurrency);
        } catch (IllegalArgumentException e) {
            result.add("Invalid base currency code");
        }
        return result;
    }
    public double convertCurrency(String from, String to, double amount) {
        double rate = hatioApiDataAccessLayer.postCurrencyExchangeRate(from, to);
        return amount * rate;
    }
}