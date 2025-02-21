package com.example.Hatio.API.dal;

import com.example.Hatio.API.controllers.HatioApiController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class HatioApiDataAccessLayer {

    HashMap<String, Integer> currencyCodeMap = new HashMap<>();
    private double convertedAmount;
    private final static String API_KEY="64ff3520ba36426c4a7caca76c5d9aa8";

    public List<Object> getExchangeRatesOfBaseCurrency(String baseCurrency) {
        String apiUrl = "https://api.exchangeratesapi.io/v1/latest?access_key="+API_KEY+"&base="+baseCurrency;
        saveCurrencyCodeInMap();
        if(currencyCodeMap.containsKey(baseCurrency)){
            String apiResponse = isApiAvailable(apiUrl);
            if (apiResponse.equals("API is unavailable")) {
                System.out.println("API is unavailable.");
                return new ArrayList<>(){{add(apiResponse);}};
            }
            RestTemplate restTemplate = new RestTemplate();
            Object apiData = restTemplate.getForObject(apiUrl, Object.class);
            return Arrays.asList(apiData);
        }
        String message = "Invalid base currency code";
        return new ArrayList<>(){{add(message);}};
    }

    public double postCurrencyExchangeRate(String from, String to) {

        String apiUrl = "https://api.exchangeratesapi.io/v1/latest?access_key="+API_KEY;
        String apiResponse = isApiAvailable(apiUrl);
        if (apiResponse.equals("API is unavailable")) {
            return -1.0;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
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
            convertedAmount=currentCurrencyRate/targetCurrencyRate;
            convertedAmount = Double.parseDouble(String.format("%.4f", convertedAmount));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedAmount;
    }

    void saveCurrencyCodeInMap() {
        String resourcePath = "data.json";
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = HatioApiController.class.getClassLoader().getResourceAsStream(resourcePath);
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
