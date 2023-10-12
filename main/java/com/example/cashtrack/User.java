package com.example.cashtrack;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;

public class User{
    public String username;
    public String email;
    public double total_balance,total_income,total_expense;
    public double limit;
    public ArrayList<Expense> expensesList;
    public ArrayList<Income> incomesList;
    public ArrayList<Balance> balancesList;

    public double getTotal_income() {
        return total_income;
    }

    public void setTotal_income(double total_income) {
        this.total_income = total_income;
    }

    public double getTotal_expense() {
        return total_expense;
    }

    public void setTotal_expense(double total_expense) {
        this.total_expense = total_expense;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotal_balance() {
        return total_balance;
    }

    public void setTotal_balance(double total_balance) {
        this.total_balance = total_balance;
    }
    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ArrayList<Expense> getExpensesList() {
        return expensesList;
    }

    public void setExpensesList(ArrayList<Expense> expensesList) {
        this.expensesList = expensesList;
    }

    public ArrayList<Income> getIncomesList() {
        return incomesList;
    }

    public void setIncomesList(ArrayList<Income> incomesList) {
        this.incomesList = incomesList;
    }

    public ArrayList<Balance> getBalancesList() {
        return balancesList;
    }

    public void setBalancesList(ArrayList<Balance> balancesList) {
        this.balancesList = balancesList;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.total_balance = 0.00;
        this.total_expense = 0.00;
        this.total_income = 0.00;
        this.limit=0.00;




        this.incomesList = new ArrayList<>();
        this.expensesList = new ArrayList<>();
        this.balancesList = new ArrayList<>();

        incomesList.add(new Income());
        expensesList.add(new Expense());
        balancesList.add(new Balance());
    }


}