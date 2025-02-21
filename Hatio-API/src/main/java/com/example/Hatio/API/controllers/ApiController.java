package com.example.Hatio.API.controllers;

import com.example.Hatio.API.entity.RequestEntity;
import com.example.Hatio.API.entity.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    HashMap<String, Integer> currencyCodeMap = new HashMap<>();
    private final static String API_KEY="64ff3520ba36426c4a7caca76c5d9aa8";
    @GetMapping("/rates")
    public List<Object> getCurrencyExchangeData(@RequestParam(defaultValue = "USD") String base) {
        String apiUrl = "https://api.exchangeratesapi.io/v1/latest?access_key="+API_KEY;
        saveCurrencyCodeInMap();
        if(currencyCodeMap.containsKey(base)){
            String apiResponse = isApiAvailable(apiUrl);
            if (apiResponse.equals("API is unavailable")) {
                System.out.println("API is unavailable.");
                return new ArrayList<>(){{add(apiResponse);}};
            }
            RestTemplate restTemplate = new RestTemplate();
            Object apiData = restTemplate.getForObject(apiUrl, Object.class);
            return Arrays.asList(apiData);
        }
        String message = "invalid base currency code";
        return new ArrayList<>(){{add(message);}};
    }

    @PostMapping("/convert")
    public List<Object> postCurrencyExchangeData(@RequestBody RequestEntity requestEntity) {
        String from = requestEntity.getFrom();
        String to = requestEntity.getTo();
        double amount = requestEntity.getAmount();
        String apiUrl = "https://api.exchangeratesapi.io/v1/latest?access_key="+API_KEY;
        String apiResponse = isApiAvailable(apiUrl);
        if (apiResponse.equals("API is unavailable")) {
            System.out.println("API is unavailable.");
            return new ArrayList<>(){{add(apiResponse);}};
        }
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            String baseCurrencyCode = rootNode.get("base").asText();

            // Extract the "rates" object
            JsonNode ratesNode = rootNode.get("rates");

            // Create a Map to store the currency codes and rates
            Map<String, Double> ratesMap = new HashMap<>();

            // Iterate through the rates object and map each key-value pair
            Iterator<Map.Entry<String, JsonNode>> fields = ratesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String currencyCode = field.getKey();
                double rate = field.getValue().asDouble();
                ratesMap.put(currencyCode, rate);
            }
            double currentCurrencyRate = ratesMap.get(from);
            double targetCurrencyRate = ratesMap.get(to);
            double convertedAmount=(currentCurrencyRate/targetCurrencyRate)*amount;
            convertedAmount = Double.parseDouble(String.format("%.4f", convertedAmount));
            responseEntity.setFrom(from);
            responseEntity.setTo(to);
            responseEntity.setAmount(amount);
            responseEntity.setConvertedAmount(convertedAmount);
            System.out.println("Base Currency: " + baseCurrencyCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(responseEntity);
    }

    void saveCurrencyCodeInMap() {
        String resourcePath = "data.json";
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = ApiController.class.getClassLoader().getResourceAsStream(resourcePath);
        try {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + resourcePath);
            }
            List<Map<String, Object>> jsonData = objectMapper.readValue(inputStream, List.class);

            for (Map<String, Object> entry : jsonData) {
                // Checking if the "currencyCode" key exists in the object
                if (entry.containsKey("currencyCode")) {
                    String currencyCode = (String) entry.get("currencyCode");
                    currencyCodeMap.put(currencyCode, 1);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String isApiAvailable(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(apiUrl, String.class);
        } catch (Exception e) {
            return "API is unavailable";
        }
    }
}