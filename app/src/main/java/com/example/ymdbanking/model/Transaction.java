package com.example.ymdbanking.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for Transaction
 * transaction can be a payment,transfer,deposit
 */
public class Transaction
{

	public enum TRANSACTION_TYPE
	{
		PAYMENT,
		TRANSFER,
		DEPOSIT,
		LOAN
	}

	public enum STATUS
	{
        APPROVED,
		PENDING,
		DENIED
	}

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd - hh:mm a");

	private String transactionID;
	private String timestamp;
	private String sendingAccount;
	private String destinationCustomer;
	private String destinationAccount;
	private String payee;
	private double amount;
	private TRANSACTION_TYPE transactionType;
	private STATUS status;

//    private long dbId;

	public Transaction()
	{
		//Empty constructor
	}

	/**
	 * Transaction constructors for payment
	 *
	 * @param transactionID - transaction ID
	 * @param payee         - receiving side of payment
	 * @param amount        - amount to transfer to payee
	 */
	public Transaction(String transactionID, String payee, double amount)
	{
		this.transactionID = transactionID;
		timestamp = DATE_FORMAT.format(new Date());
		this.payee = payee;
		this.amount = amount;
		transactionType = TRANSACTION_TYPE.PAYMENT;
	}

	public Transaction(String transactionID, String timestamp, String payee, double amount, long dbId)
	{
		this(transactionID, payee, amount);
		this.timestamp = timestamp;
//		this.dbId = dbId;
	}

	/**
	 * Transactions constructors for deposit
	 *
	 * @param transactionID - transaction ID
	 * @param amount        - amount to deposit to account
	 */
	public Transaction(String transactionID, double amount, Account destinationAccount)
	{
		this.transactionID = transactionID;
		timestamp = DATE_FORMAT.format(new Date());
		this.amount = amount;
		transactionType = TRANSACTION_TYPE.DEPOSIT;
		this.destinationAccount = destinationAccount.getAccountNo();
	}

	public Transaction(String transactionID, String timestamp, Account destinationAccount, double amount)
	{
		this(transactionID, amount, destinationAccount);
		this.timestamp = timestamp;
	}

	/**
	 * Transaction constructors for loan
	 *
	 * @param transactionID      - transaction ID
	 * @param amount             - amount of the loan
	 * @param DestinationAccount - receiving account for loan
	 */
	public Transaction(String transactionID, Account DestinationAccount, double amount)
	{
		this.transactionID = transactionID;
		timestamp = DATE_FORMAT.format(new Date());
		this.amount = amount;
		this.destinationAccount = DestinationAccount.toTransactionString();
		transactionType = TRANSACTION_TYPE.LOAN;
		status = STATUS.PENDING;
	}

	public Transaction(String transactionID, Account DestinationAccount, String timestamp, double amount, long dbId)
	{
		this(transactionID, DestinationAccount, amount);
		this.timestamp = timestamp;
//		this.dbId = dbId;
	}

	/**
	 * Transaction constructors for transfer
	 *
	 * @param transactionID      - transaction ID
	 * @param sendingAccount     - sending account for transfer
	 * @param destinationAccount - receiving account
	 * @param amount             - amount to transfer to destinationAccount
	 */
	public Transaction(String transactionID, String sendingAccount, String destinationAccount, double amount)
	{
		this.transactionID = transactionID;
		this.timestamp = DATE_FORMAT.format(new Date());
		this.sendingAccount = sendingAccount;
		this.destinationAccount = destinationAccount;
		this.amount = amount;
		transactionType = TRANSACTION_TYPE.TRANSFER;
	}

	public Transaction(String transactionID, String timestamp, String sendingAccount, String destinationCustomer, String destinationAccount, double amount, long dbId)
	{
		this(transactionID, sendingAccount, destinationAccount, amount);
		this.timestamp = timestamp;
//		this.dbId = dbId;
	}

	/**
	 * getters used to access the private fields of the transaction
	 */
	public String getTransactionID() { return transactionID; }

	public String getTimestamp() { return timestamp; }

	public String getSendingAccount()
	{
		return sendingAccount;
	}

	public String getDestinationAccount()
	{
		return destinationAccount;
	}

	public String getPayee() { return payee; }

	public double getAmount()
	{
		return amount;
	}

	public TRANSACTION_TYPE getTransactionType()
	{
		return transactionType;
	}

	public STATUS getStatus() {return status;}

	//	public void setDbId(long dbId) { this.dbId = dbId; }

}
