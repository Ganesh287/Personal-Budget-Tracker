package com.example.cashtrack;

import java.sql.Time;
import java.util.Date;

public class Income  {
    public double amount;
    public String date;
    public String time;
    public String incometype;


    // empty constructor
    public Income(){
        this.amount = 0.00;
        this.date = "00";
        this.time = "00";
        this.incometype = "Other";
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

    public String getIncometype() {
        return incometype;
    }

    public void setIncometype(String incometype) {
        this.incometype = incometype;
    }

    public Income(double amount, String date, String time, String incometype) {
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.incometype = incometype;
    }


}
