package com.example.ymdbanking.model;

import android.content.Context;

import java.util.ArrayList;

import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.*;

public class Clerk extends User
{
	private final static int typeID = 2;
	private ArrayList<Customer> customers;
	private ArrayList<Transaction> loansToApprove;
	private ArrayList<Transaction> cashDepositsToApprove;

	public Clerk()
	{
		//Empty constructor
	}

	public Clerk(String email,String fullName,String id,String password,String phone,String username)
	{
		super(email,fullName,id,password,phone,username,typeID);
		this.customers = new ArrayList<>(0);
		this.loansToApprove = new ArrayList<>(0);
	}

	public Clerk(String email,String fullName,String id,String password,String phone,String username
			,ArrayList<Customer> customers,ArrayList<Transaction> loansToApprove)
	{
		super(email,fullName,id,password,phone,username,typeID);
		this.customers = customers;
		this.loansToApprove = loansToApprove;
	}

	// getters and setters
	public ArrayList<Transaction> getLoansToApprove() {return loansToApprove;}
	public void setLoansToApprove(ArrayList<Transaction> loansToApprove) {this.loansToApprove = loansToApprove;}
	public void setCustomers(ArrayList<Customer> customers) {this.customers = customers;}
//	public static int getTypeID() {return typeID;}
	public ArrayList<Customer> getCustomers() {return customers;}

	// methods
//	public ArrayList<Customer> getUsers(int id, Context context)
//	{
//		ApplicationDB applicationDB = new ApplicationDB(context);
//		return applicationDB.getClerkCustomers(id);
//	}

	public void assignCustomerToClerk(Customer customer,Context context)
	{
		customers.add(customer);
		ApplicationDB applicationDB = new ApplicationDB(context);
		applicationDB.saveCustomerToClerkList(customer,this);
	}

	public void viewCustomerAccounts(Customer customer)
	{

	}

	public void addAccount(Customer customer, String accountName, double accountBalance)
	{
		String accNo = "A" + (customer.getAccounts().size() + 1);
		Account account = new Account(accountName, accNo, accountBalance);
		customer.getAccounts().add(account);
	}

	public void addLoanTransaction(String customerId,Account destinationAccount, double amount)
	{
//		destinationAccount.setAccountBalance(destinationAccount.getAccountBalance() + amount);
		int receivingAccLoanCount = 0;
		try
		{
			for (int i = 0; i < destinationAccount.getTransactions().size(); i++)
			{
				if (destinationAccount.getTransactions().get(i).getTransactionType() ==
				    Transaction.TRANSACTION_TYPE.LOAN)
				{
					receivingAccLoanCount++;
				}
			}
			Transaction transaction = new Transaction("T" + (destinationAccount.getTransactions().size() + 1) + "-L" + (receivingAccLoanCount + 1), destinationAccount, amount,customerId);
//		destinationAccount.getTransactions().put(transaction.getTransactionID(),transaction);
			destinationAccount.getTransactions().add(transaction);
//			addLoanForPending(destinationAccount.getTransactions().get(destinationAccount.getTransactions().size() - 1));
			addLoanForPending(transaction);
		}
		catch(NullPointerException e)
		{
			destinationAccount.setTransactions(new ArrayList<Transaction>());
			Transaction transaction = new Transaction("T1" + "-L1", destinationAccount, amount,customerId);
			destinationAccount.getTransactions().add(transaction);
			addLoanForPending(transaction);
		}
	}

	public void addCashDepositTransaction(String customerId,Account destinationAccount,double amount)
	{
		int receivingAccDepositCount = 0;
		try
		{
			for (int i = 0; i < destinationAccount.getTransactions().size(); i++)
			{
				if (destinationAccount.getTransactions().get(i).getTransactionType() ==
				    Transaction.TRANSACTION_TYPE.DEPOSIT)
				{
					receivingAccDepositCount++;
				}
			}
			Transaction transaction = new Transaction("T" + (destinationAccount.getTransactions().size() + 1) + "-D" + (receivingAccDepositCount + 1), destinationAccount, amount,customerId);
			destinationAccount.getTransactions().add(transaction);
			addCashDepositForPending(transaction);
		}
		catch(NullPointerException e)
		{
			destinationAccount.setTransactions(new ArrayList<Transaction>());
			Transaction transaction = new Transaction("T1" + "-D1", destinationAccount, amount,customerId);
			destinationAccount.getTransactions().add(transaction);
			addCashDepositForPending(transaction);
		}
	}

	public void addLoanForPending(Transaction pendingLoan)
	{
		loansToApprove.add(pendingLoan);
	}
	public void addCashDepositForPending(Transaction pendingDeposit) {cashDepositsToApprove.add(pendingDeposit);}

	@Override
	public String toString()
	{
		return getFullName();
	}
}
