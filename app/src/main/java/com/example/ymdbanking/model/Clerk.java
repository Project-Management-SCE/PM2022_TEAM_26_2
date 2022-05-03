package com.example.ymdbanking.model;

import java.util.ArrayList;
import com.example.ymdbanking.model.*;

public class Clerk extends User
{
//	private String firstName;
//	private String lastName;
//	private String country;
//	private String username;
//	private String password;
	private final static int typeID = 2;
	private ArrayList<Customer> customers;
	private ArrayList<Transaction> loansToApprove;
//	private long dbId;

	public Clerk(String email,String fullName,String id,String password,String phone,String username)
	{
		super(email,fullName,id,password,phone,username);
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.country = country;
		this.customers = new ArrayList<>(0);
		this.loansToApprove = new ArrayList<>(0);
	}

	public Clerk(String email,String fullName,String id,String password,String phone,String username,
	             ArrayList<Customer> customers,ArrayList<Transaction> loansToApprove)
	{
		super(email,fullName,id,password,phone,username);
		this.customers = customers;
		this.loansToApprove = loansToApprove;
	}

	// getters and setters
//	public String getFirstName() {return firstName;}
//	public void setFirstName(String firstName) {this.firstName = firstName;}
//	public String getLastName() {return lastName;}
//	public void setLastName(String lastName) {this.lastName = lastName;}
//	public String getUsername() {return username;}
//	public void setUsername(String username) {this.username = username;}
//	public String getPassword() {return password;}
//	public void setPassword(String password) {this.password = password;}
	public ArrayList<Transaction> getLoansToApprove() {return loansToApprove;}
	public void setLoansToApprove(ArrayList<Transaction> loansToApprove) {this.loansToApprove = loansToApprove;}
	public void setCustomers(ArrayList<Customer> customers) {this.customers = customers;}
	public static int getTypeID() {return typeID;}
	public ArrayList<Customer> getCustomers() {return customers;}
	//	public String getCountry() {return country;}
//	public void setCountry(String country) {this.country = country;}
//	public long getDbId() {return dbId;}
//	public void setDbId(long dbId) {this.dbId = dbId;}

	// methods
//	public ArrayList<Customer> getUsers(int id, Context context)
//	{
//		ApplicationDB applicationDB = new ApplicationDB(context);
//		return applicationDB.getClerkCustomers(id);
//	}
//	public void assignProfileToCustomer(Customer customer, Context context)
//	{
//		customers.add(customer);
//		ApplicationDB applicationDB = new ApplicationDB(context);
//		applicationDB.saveCustomerToClerkList(customer, getUsername());
//	}

	public void viewCustomerAccounts(Customer customer)
	{

	}

	public void addAccount(Customer customer, String accountName, double accountBalance)
	{
		String accNo = "A" + (customer.getAccounts().size() + 1);
		Account account = new Account(accountName, accNo, accountBalance);
		customer.getAccounts().put(accNo,account);
	}

	//TODO: need to implement clerk methods
	public void addLoanTransaction(Account destinationAccount, double amount)
	{
//		destinationAccount.setAccountBalance(destinationAccount.getAccountBalance() + amount);
		int receivingAccTransferCount = 0;
		for (int i = 0; i < destinationAccount.getTransactions().size(); i ++)
		{
			if (destinationAccount.getTransactions().get(i).getTransactionType() == Transaction.TRANSACTION_TYPE.LOAN)
			{
				receivingAccTransferCount++;
			}
		}
		destinationAccount.getTransactions().add(new Transaction("T" + (destinationAccount.getTransactions().size() + 1) + "-L" + (receivingAccTransferCount + 1), destinationAccount, amount));
		addLoanForPending(destinationAccount.getTransactions().get(destinationAccount.getTransactions().size()));
	}

	public void addLoanForPending(Transaction pendingLoan)
	{
		loansToApprove.add(pendingLoan);
	}

	@Override
	public String toString()
	{
		return getFullName();
	}
}
