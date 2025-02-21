package com.example.Hatio.API.entity;

public class ResponseEntity {
    private String from;
    private String to;
    private double amount;
    private double convertedAmount;

    public ResponseEntity(){}
    public void setFrom(String from) {
        this.from = from;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
