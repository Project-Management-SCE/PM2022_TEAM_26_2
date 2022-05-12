package com.example.ymdbanking.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class Profile for profile users
 * extended by User Class
 */
public class Customer extends User
{
    private final static int typeID = 3;
    private ArrayList<Account> accounts;
    private ArrayList<Payee> payees;

    public Customer()
    {
        //Empty constructor
    }

    /**
     * Constructor for creating profile objects to hold existing profiles data
     * and to view them on a list
     */
    public Customer(String email,String fullName,String id,String password,String phone,String username)
    {
        super(email,fullName,id,password,phone,username,typeID);

        accounts = new ArrayList<>(0);
        payees = new ArrayList<>(0);
    }

    /**
     * Constructor for creating a profile class user without setting the dbId field
     * dbId field will be set later on
     */
    public Customer(String email,String fullName,String id,String password,String phone,String username,
                    int typeID,ArrayList<Account> accounts,ArrayList<Payee> payees)
    {
        super(email,fullName,id,password,phone,username,typeID);
        this.accounts = accounts;
        this.payees = payees;
    }

    /**
     * getters used to access the private fields of the profile
     */
//    public String getFirstName() {
//        return firstName;
//    }
//    public String getLastName() {
//        return lastName;
//    }
//    public String getCountry() {
//        return country;
//    }
//    public String getUsername() {
//        return username;
//    }
//    public String getPassword() {
//        return password;
//    }
    public ArrayList<Account> getAccounts() { return accounts; }
    public void setAccounts(ArrayList<Account> accounts) {this.accounts = accounts;}
    public ArrayList<Payee> getPayees() { return payees; }
//    public static int getTypeID() {return typeID;}
    //    public long getDbId() { return dbId; }
//    public void setDbId(long dbId) { this.dbId = dbId; }
//    public void setFirstName(String firstName) {this.firstName = firstName;}
//    public void setLastName(String lastName) {this.lastName = lastName;}
//    public void setCountry(String country) {this.country = country;}
//    public void setUsername(String username) {this.username = username;}
//    public void setPassword(String password) {this.password = password;}

    /**
     * Method to add account to this profile user
     * @param accountName - account of the profile
     * @param accountBalance - account balance (current amount in account)
     */
    public void addAccount(String accountName, double accountBalance)
    {
        String accNo = "A" + (accounts.size() + 1);
        Account account = new Account(accountName, accNo, accountBalance);
        accounts.add(account);
    }
//    public void setAccountsFromDB(ArrayList<Account> accounts) {
//        this.accounts = accounts;
//    }

    /**
     * Method to implement the transfer logic
     * and adds the transfer to profile's transactions
     * @param sendingAcc - account of sending profile
     * @param receivingAcc - account of receiving profile
     * @param transferAmount - amount to transfer to receiving account

     */
    public void addTransferTransaction(Account sendingAcc,Account receivingAcc, double transferAmount)
    {
        sendingAcc.setAccountBalance(sendingAcc.getAccountBalance() - transferAmount);
        receivingAcc.setAccountBalance(receivingAcc.getAccountBalance() + transferAmount);

        int sendingAccTransferCount = 0;
        int receivingAccTransferCount = 0;
        for (int i = 0; i < sendingAcc.getTransactions().size(); i ++) {
            if (sendingAcc.getTransactions().get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                sendingAccTransferCount++;
            }
        }
        for (int i = 0; i < receivingAcc.getTransactions().size(); i++) {
            if (receivingAcc.getTransactions().get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                receivingAccTransferCount++;
            }
        }

        sendingAcc.getTransactions().add(new Transaction("T" + (sendingAcc.getTransactions().size() + 1) + "-T" + (sendingAccTransferCount+1), sendingAcc.toTransactionString(), receivingAcc.toTransactionString(), transferAmount));
        receivingAcc.getTransactions().add(new Transaction("T" + (receivingAcc.getTransactions().size() + 1) + "-T" + (receivingAccTransferCount+1), sendingAcc.toTransactionString(), receivingAcc.toTransactionString(), transferAmount));
    }

    /**
     * Add payee to profile's list of payees
     * @param payeeName
     */
    public void addPayee(String payeeName)
    {
        String payeeID = "P" + (payees.size() + 1);
        Payee payee = new Payee(payeeID, payeeName);
        payees.add(payee);
    }

//    public int getNumberOfAccounts() { return this.accounts.size(); }
    public void setPayeesFromDB(ArrayList<Payee> payees) {
        this.payees = payees;
    }

    @Override
    public String toString()
    {
        return getUsername();
    }
}
