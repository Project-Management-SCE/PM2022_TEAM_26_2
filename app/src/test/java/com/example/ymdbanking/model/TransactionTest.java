package com.example.ymdbanking.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class TransactionTest
{
	private Customer customer;
	private Transaction transaction;
	private Account account;

	@Before
	public void setUp()
	{
		//Setting customer info
		String email = "daniel@gmail.com";
		String fullName = "Daniel Arbiv";
		String id = "12345689";
		String password = "123456";
		String phone = "12345678";
		String username = "daniel";
		String country = "USA";
		customer = new Customer(email,fullName,id,password,phone,username,country);
//		//Setting Account info
//		String accountName = "Daniel-1";
//		String accountNo = "A1";
//		double initBalance = 2000;
//		account = new Account(accountName,accountNo,initBalance);
	}

	@Test
	public void testTransfer()
	{
		//Sending account details
		String accountName = "Daniel-1",accountNo = "A1";
		double initBalance = 2000;
		customer.addAccount(accountName,initBalance);
		//Sending Account
		Account sendingAccount = customer.getAccounts().get(customer.getAccounts().size() - 1);
//		String accountName1 = "Daniel-1",accountNo1 = "A-1";
//		double initBalance1 = 2000;
		//Receiving account
		String accountName2 = "Mazal-1",accountNo2 = "A-1";
		double initBalance2 = 2500;
		Account receivingAccount = new Account(accountName2,accountNo2,initBalance2);
		double transferAmount = 2000;
		customer.addTransferTransaction(sendingAccount,receivingAccount,transferAmount);
		double expectedAmount = 4500;
		double actualAmount = receivingAccount.getAccountBalance();
		assertEquals(expectedAmount,actualAmount,0.0001);
	}

	@Test
	public void testTransferOverAccountAmount()
	{
		//Sending account details
		String accountName = "Daniel-1",accountNo = "A1";
		double initBalance = 2000;
		customer.addAccount(accountName,initBalance);
		//Sending Account
		Account sendingAccount = customer.getAccounts().get(customer.getAccounts().size() - 1);
//		String accountName1 = "Daniel-1",accountNo1 = "A1";
//		double initBalance1 = 2000;
		//Receiving account
		String accountName2 = "Mazal-1",accountNo2 = "A-1";
		double initBalance2 = 2500;
		Account receivingAccount = new Account(accountName2,accountNo2,initBalance2);
		//Transfer amount is over account's balance by 1
		//Should not approve transfer so balance of receiving account should stay the same
		double transferAmount = 2001;
		customer.addTransferTransaction(sendingAccount,receivingAccount,transferAmount);
		double expectedAmount = 2500;
		double actualAmount = receivingAccount.getAccountBalance();
		assertEquals(expectedAmount,actualAmount,0.0001);
	}
}
