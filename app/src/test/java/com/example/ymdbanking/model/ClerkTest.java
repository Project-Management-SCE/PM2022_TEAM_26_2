package com.example.ymdbanking.model;

import android.content.Context;

import junit.framework.TestCase;

import java.util.ArrayList;

public class ClerkTest extends TestCase
{
	String email = "daniel@gmail.com",fullName = "Daniel Arbiv",id = "123456789",
			password = "123456",phone = "05050000",username = "daniel";
	Clerk clerk = new Clerk(email,fullName,id,password,phone,username);

	public void testGetLoansToApprove()
	{
		//Validate loans arrayList size
		int expectedSize = 0;
		int actualSize = clerk.getLoansToApprove().size();
		assertEquals(expectedSize,actualSize);
	}

	public void testSetLoansToApprove()
	{
		String customerId = "123456789",accountName = "Daniel-1",accountNo = "A-1";
		String transactionID = "T1-L1";
		double initBalance = 1000,loanAmount = 2000;
		Account account = new Account(accountName,accountNo,initBalance);

		clerk.addLoanTransaction(customerId,account,loanAmount);

		//Validate arrayList size
		int expectedSize = 1;
		int actualSize = clerk.getLoansToApprove().size();
		assertEquals(expectedSize,actualSize);
		//Get transaction for validation
		Transaction transaction = clerk.getLoansToApprove().get(clerk.getLoansToApprove().size() - 1);
		//Validate loan transaction
		//Validate customerId
		assertEquals(customerId,transaction.getDestinationCustomerId());
		//Validate accountNo
		assertEquals(accountNo,transaction.getDestinationAccount());
		//Validate accountName
		assertEquals(transactionID,transaction.getTransactionID());
		//Validate amount
		assertEquals(loanAmount,transaction.getAmount());
		//Validate transaction type
		assertEquals(Transaction.TRANSACTION_TYPE.LOAN,transaction.getTransactionType());
	}

	public void testGetCustomers()
	{
		int initialCustomersSize = 0;
		assertEquals(initialCustomersSize,clerk.getCustomers().size());
	}

	public void testSetCustomers()
	{
		//Reset arrayList
		clerk.setCustomers(new ArrayList<>(0));

		clerk.getCustomers().add(new Customer("daniel@gmail.com","Daniel Arbiv","123456789",
				"123456","0501232130","daniel","USA"));
		int expectedSize = 1;
		int actualSize = clerk.getCustomers().size();
		assertEquals(expectedSize,actualSize);
		//Reset arrayList
		clerk.setCustomers(new ArrayList<>(0));
	}

	public void testAssignCustomerToClerk()
	{
		//Add customer to arrayList for test
		clerk.assignCustomerToClerk(new Customer("daniel@gmail.com","Daniel Arbiv","123456789",
				"123456","0501232130","daniel","USA"),null);
		//Validate customer's info in customers arrayList
		Customer customer = clerk.getCustomers().get(clerk.getCustomers().size() - 1);
		//Validate email
		assertEquals("daniel@gmail.com",customer.getEmail());
		//Validate fullName
		assertEquals("Daniel Arbiv",customer.getFullName());
		//Validate id
		assertEquals("123456789",customer.getId());
		//Validate password
		assertEquals("123456",customer.getPassword());
		//Validate phone
		assertEquals("0501232130",customer.getPhone());
		//Validate username
		assertEquals("daniel",customer.getUsername());
		//Validate country
		assertEquals("USA",customer.getCountry());
	}

	public void testAddLoanTransaction()
	{
		//Reset arrayList for test
		clerk.setLoansToApprove(new ArrayList<>(0));

		String customerId = "123456789";
		String accountName = "Daniel-1",accountNo = "A-1";
		double initialBalance = 1000;
		Account account = new Account(accountName,accountNo,initialBalance);
		double loanAmount = 2500;

		//Add loan transaction
		clerk.addLoanTransaction(customerId,account,loanAmount);
		//Get loan transaction
		Transaction transaction = clerk.getLoansToApprove().get(clerk.getLoansToApprove().size() - 1);
		//Validate transaction's info
		String transactionID = "T1-L1";
		//Validate transactionID
		assertEquals(transactionID,transaction.getTransactionID());
		//Validate destinationAccount
		assertEquals(accountNo,transaction.getDestinationAccount());
		//Validate amount
		assertEquals(loanAmount,transaction.getAmount());
		//Validate customerId
		assertEquals(customerId,transaction.getDestinationCustomerId());

		//Reset arrayList for test
		clerk.setLoansToApprove(new ArrayList<>(0));
	}

	public void testAddCashDepositTransaction()
	{

	}

	public void testAddLoanForPending()
	{
	}

	public void testAddCashDepositForPending()
	{
	}
}