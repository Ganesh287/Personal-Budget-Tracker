package com.example.cashtrack;

import java.sql.Time;
import java.util.Date;

public class Expense  {
    public double getAmount() {
        return amount;
    }

    public Expense(){
//        this.amount = 0.00;
//        this.date = "date";
//        this.time = "time";
//        this.expenseType = "Other";
    }

    public Expense(double amount, String date, String time, String expenseType) {
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.expenseType = expenseType;
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

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public double amount;
    public String date;
    public String time;
    public String expenseType;
}
