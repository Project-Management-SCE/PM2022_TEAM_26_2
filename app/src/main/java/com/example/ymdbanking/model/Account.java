package com.example.ymdbanking.model;

import android.icu.text.Edits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Class used to create an account for the user
 */

public class Account
{
    private String accountName;
    private String accountNo;
    private double accountBalance;
    private ArrayList<Transaction> transactions;
//    private long dbID;

    public Account()
    {
        //Empty constructor
    }

    public Account(String accountName, String accountNo, double accountBalance)
    {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        transactions = new ArrayList<>(0);
    }

    public Account(String accountName, String accountNo, double accountBalance,ArrayList<Transaction> transactions)
    {
        this(accountName, accountNo, accountBalance);
        this.transactions = transactions;
    }

    /**
     * Getters for the account name, number and balance
     */
    public String getAccountName() {
        return accountName;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    public void setAccountName(String accountName) {this.accountName = accountName;}
    public void setAccountNo(String accountNo) {this.accountNo = accountNo;}
    //    public void setDbID(long dbID) { this.dbID = dbID; }

    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Method to implement payment to payee
     * Adds a payment to transactions
     * @param payee - payee's name
     * @param amount - the amount to transfer to payee
     */
    public void addPaymentTransaction (String payee, double amount)
    {
        accountBalance -= amount;

        int paymentCount = 0;

        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT)  {
                paymentCount++;
            }
        }

        Transaction payment = new Transaction("T" + (transactions.size() + 1) + "-P" + (paymentCount+1), payee, amount);
        transactions.add(payment);
//        transactions.put(payment.getTransactionID(),payment);
    }

    /**
     * Method to implement a deposit to profile's account
     * @param amount - amount to deposit in account
     */
    public void addDepositTransaction(double amount)
    {
        accountBalance += amount;

        //TODO: Could be a better way - ie. each time a deposit is added, add it to the master count (global variable - persisted?)
        int depositsCount = 0;

        for (int i = 0; i < transactions.size(); i++)
        {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.DEPOSIT)
            {
                depositsCount++;
            }
        }

        Transaction deposit = new Transaction("T" + (transactions.size() + 1) + "-D" + (depositsCount+1),amount,this);
        transactions.add(deposit);
//        transactions.put(deposit.getTransactionID(),deposit);
    }

    public void addLoanTransaction(double amount)
    {
        accountBalance += amount;

        //TODO: Could be a better way - ie. each time a deposit is added, add it to the master count (global variable - persisted?)
        int depositsCount = 0;

        for (int i = 0; i < transactions.size(); i++)
        {
            if (transactions.get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.LOAN)
            {
                depositsCount++;
            }
        }

        Transaction loan = new Transaction("T" + (transactions.size() + 1) + "-L" + (depositsCount+1),this,amount);
        transactions.add(loan);
//        transactions.put(loan.getTransactionID(),loan);
    }

    /**
     * toString will be used by the account adapter
     * @return
     */
    public String toString()
    {
        return (accountName + " ($" + String.format(Locale.getDefault(), "%.2f",accountBalance) + ")");
    }
    /**
     * toString will be used by the account adapter
     * @return
     */
    public String toTransactionString() { return (accountName + " (" + accountNo + ")"); }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}