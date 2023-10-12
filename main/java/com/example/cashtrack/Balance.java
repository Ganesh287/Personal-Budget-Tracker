package com.example.cashtrack;

public class Balance {
    public double amount;
    public String date;
    public String time;
    public String balanceType;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    private String transactionId;

    public Balance(String transactionId) {
        this.transactionId = transactionId;
    }

    public Balance() {
        this.amount = 0.00;
        this.date = "01/01/1000";
        this.time = "12:00";
        this.balanceType = "Other";
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public Balance(double amount, String date, String time, String balanceType) {
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.balanceType = balanceType;
    }
}
