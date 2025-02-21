package com.example.Hatio.API;

import com.example.Hatio.API.dal.HatioApiDataAccessLayer;
import com.example.Hatio.API.services.HatioApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


class HatioApiServiceTest {
    @Mock
    private HatioApiDataAccessLayer hatioApiDataAccessLayer;

    @InjectMocks
    private HatioApiService hatioApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertCurrency() {
        String from = "USD";
        String to = "EUR";
        double amount = 100.0;
        double exchangeRate = 0.85;

        when(hatioApiDataAccessLayer.postCurrencyExchangeRate(from, to)).thenReturn(exchangeRate);

        double result = hatioApiService.convertCurrency(from, to, amount);

        assertEquals(85.0, result, "Conversion is incorrect");
    }

    @Test
    void exchangeRatesOfBaseCurrency() {
        String baseCurrency = "USD";

        when(hatioApiDataAccessLayer.getExchangeRatesOfBaseCurrency(baseCurrency)).thenThrow(new IllegalArgumentException("Unsupported currency pair"));

        List<Object> result = hatioApiService.ratesOfBaseCurrency(baseCurrency);

        assertEquals("Invalid base currency code", result.get(0),"Base currency invalid");
    }
}