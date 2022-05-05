package com.example.ymdbanking.db;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.ymdbanking.model.Account;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.example.ymdbanking.model.Payee;
import com.example.ymdbanking.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationDB
{
	//Fields
//	private DatabaseReference dbRef;
	private FirebaseDatabase database;
//	private FirebaseFirestore firestore;
	private Context context;

	//Users Collection
	private final String USERS = "Users";

	//Users Collection Fields
	private static final String EMAIL = "email";
	private static final String FULL_NAME = "fullName";
	private static final String ID = "id";
	private static final String PASSWORD = "password";
	private static final String PHONE = "phone";
	private static final String USERNAME = "username";
	private static final String KEY_TRANSACTIONS = "transactions";
	private static final String ACCOUNTS = "accounts";
	private static final String KEY_PAYEES = "payees";
	private static final String KEY_PAYEE_ID = "payee_id";
	private static final String KEY_PAYEE_NAME = "payee_name";

	//Account Collection Fields
	private final String KEY_ACCOUNT_NAME = "account_name";
	private static final String KEY_ACCOUNT_NUM = "account_number";
	private final String KEY_ACCOUNT_BALANCE = "balance";

	//Clerks Collection
	private final String CLERKS = "Clerks";


	/**
	 * Constructor
	 */
	public ApplicationDB(Context context)
	{
		this.database = FirebaseDatabase.getInstance();
//		firestore = FirebaseFirestore.getInstance();
		this.context = context;
	}

	//Methods
	/**
	 * Method gets customer and assigns him to a clerk
	 * @param customer - customer object
	 * @param clerk - clerk object
	 */
	public void saveCustomerToClerkList(Customer customer, Clerk clerk)
	{
		HashMap<String,Object> cust = new HashMap<>();
		cust.put("email",customer.getEmail());
		cust.put("fullName",customer.getFullName());
		cust.put("id",customer.getId());
		cust.put("password",customer.getPassword());
		cust.put("phone",customer.getPhone());
		cust.put("username",customer.getUsername());
		database.getReference("Clerks").child(clerk.getUsername()).child("customers")
				.child(String.valueOf(customer.getId())).setValue(cust);
	}

	/**
	 * Method to save transaction to database
	 * @param sendingCustomer - sending customer
	 * @param transaction - transaction object to hold all info about the transaction
	 */
	public void saveNewTransaction(Customer sendingCustomer, Transaction transaction)
	{
		HashMap<String,Transaction> tran = new HashMap<>();
//		tran.put("transaction_id", transaction.getTransactionID());
//		tran.put("transaction_time", transaction.getTimestamp());
//		tran.put("amount",transaction.getAmount());
//		tran.put("type",transaction.getTransactionType().toString());

		if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER || transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT)
		{
//			tran.put("sending_account",transaction.getSendingAccount());
//			tran.put("destination_account",transaction.getDestinationAccount());
//			database.getReference("Accounts").child(sendingCustomer.getId())
//					.child(transaction.getSendingAccount()).setValue(tran);
			tran.put(transaction.getTransactionID(),transaction);
			database.getReference("Accounts").child(sendingCustomer.getId())
					.child(transaction.getSendingAccount()).setValue(tran);

		}
//		else if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT)
//		{
//			tran.put("sending_account",transaction.getSendingAccount());
//			tran.put("destination_account",transaction.getDestinationAccount());
//			tran.put("payee",transaction.getPayee());
//			database.getReference("Accounts").child(sendingCustomer.getId())
//					.child(transaction.getSendingAccount()).setValue(tran);
//			database.getReference("Accounts").child(sendingCustomer.getId())
//					.child(transaction.getSendingAccount()).setValue(transaction);
//		}
		else if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.DEPOSIT || transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.LOAN)
		{
//			tran.put("destination_account", transaction.getDestinationAccount());
//			database.getReference("Accounts").child(sendingCustomer.getId())
//					.child("transactions").child(transaction.getDestinationAccount()).setValue(tran);
			tran.put(transaction.getTransactionID(),transaction);
			database.getReference("Accounts").child(sendingCustomer.getId())
					.child(transaction.getDestinationAccount()).child("transactions").setValue(tran);
		}

//		database.getReference("Users").child(sendingCustomer.getId()).child("accounts")
//				.child(transaction.getDestinationAccount()).child("transactions")
//				.child(transaction.getTransactionID()).setValue(tran);
	}

	/**
	 * Method to save new account to user's account collection in database
	 * @param customer - customer object to add the account
	 * @param account - account object to be added to customer
	 */
	public void saveNewAccount(Customer customer, Account account)
	{
//		HashMap<String,Account> newAccount = new HashMap<>();
//		newAccount.put(account.getAccountNo(),account);
		database.getReference("Accounts").child(customer.getId()).child(account.getAccountNo())
			.setValue(account);

	}

	/**
	 * Method to overwrite existing account from customer accounts
	 * @param customer - customer object
	 * @param account - account object
	 */
	public void overwriteAccount(Customer customer,Account account)
	{
		HashMap<String,Object> newAccount = new HashMap<>();
		newAccount.put("account_name",account.getAccountName());
		newAccount.put("account_balance",account.getAccountBalance());
		newAccount.put("transactions",account.getTransactions());

		database.getReference("Accounts").child(customer.getId()).child(account.getAccountNo())
				.updateChildren(newAccount);
	}

	public void saveNewPayee(Customer customer, Payee payee)
	{
		final long[] id = new long[1];
		HashMap<String,Object> newPayee = new HashMap<>();

		database.getReference(USERS).child(KEY_PAYEES).get()
				.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<DataSnapshot> task)
					{
						for(DataSnapshot ds : task.getResult().getChildren())
						{
							if(!task.getResult().getChildren().iterator().hasNext())
							{
								id[0] = Long.parseLong(ds.getValue(Payee.class).getPayeeID()) + 1;
								newPayee.put(KEY_PAYEE_ID,id[0]);
								newPayee.put(KEY_PAYEE_NAME,payee.getPayeeName());
								database.getReference(USERS).child(String.valueOf(customer.getId()))
										.child(KEY_PAYEES).child(payee.getPayeeID()).setValue(newPayee);
							}
						}

					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't save new payee to DB: " + e.toString(), Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});
	}

	/**
	 * Method to get all admins from database
	 * @return - ArrayList of admins
	 */
	public ArrayList<Admin> getAllAdmins()
	{
		final ArrayList<Admin> admins = new ArrayList<>();
		database.getReference("Admins").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					admins.add(ds.getValue(Admin.class));
			}
		})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get all admins from DB: " + e.toString(), Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return admins;
	}

	/**
	 * Method to get all clerks from database
	 * @return - ArrayList of clerks
	 */
	public ArrayList<Clerk> getAllClerks()
	{
		ArrayList<Clerk> clerks = new ArrayList<>();
		database.getReference("Clerks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					clerks.add(ds.getValue(Clerk.class));
			}
		})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get all clerks from DB", Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return clerks;
	}

	public ArrayList<Customer> getClerkCustomers(Clerk clerk)
	{
		ArrayList<Customer> customers = new ArrayList<>();
		database.getReference("Clerks").child(clerk.getUsername()).child("customers").get()
				.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<DataSnapshot> task)
					{
						for(DataSnapshot ds : task.getResult().getChildren())
							customers.add(ds.getValue(Customer.class));
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get clerk's customers from DB", Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return customers;
	}

	public ArrayList<Customer> getAllCustomers()
	{
		ArrayList<Customer> customers = new ArrayList<>();
		database.getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					customers.add(ds.getValue(Customer.class));
			}
		})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get all customers from DB", Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return customers;
	}

	public ArrayList<Customer> getAllCustomersForTransfer(String customerID)
	{
		ArrayList<Customer> customers = new ArrayList<>();
		database.getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
				{
					customers.add(ds.getValue(Customer.class));
				}
				customers.removeIf(customer -> customer.getId().equals(customerID));
			}
		})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get customers for transfer from DB", Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return customers;
	}

//	public ArrayList<Account> getAllAccountsForTransfer(String customerID)
//	{
//		ArrayList<Account> accounts = new ArrayList<>();
//		database.getReference(customerID).child(KEY_ACCOUNTS).get()
//				.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
//				{
//					@Override
//					public void onComplete(@NonNull Task<DataSnapshot> task)
//					{
//						for(DataSnapshot ds : task.getResult().getChildren())
//							accounts.add(ds.getValue(Account.class));
//					}
//				})
//				.addOnFailureListener(new OnFailureListener()
//				{
//					@Override
//					public void onFailure(@NonNull Exception e)
//					{
//						Toast.makeText(context, "ERROR - Can't get customer's accounts from DB", Toast.LENGTH_SHORT).show();
//						Log.d("DB_ERROR",e.toString());
//					}
//				});
//
//		return accounts;
//	}

	public ArrayList<Account> getAccountsFromCurrentCustomer(String customerID)
	{
		HashMap<String,Account> accountHM = new HashMap<>();
		ArrayList<Account> accounts = new ArrayList<>();
		database.getReference("Accounts").child(customerID)
			.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot)
			{
				for(DataSnapshot ds : snapshot.getChildren())
				{
//					accountHM.put(ds.getKey(),ds.getValue(Account.class));
//					accounts.add(accountHM.get(ds.getKey()));
					accounts.add(new Account(
							ds.child("accountName").getValue(String.class),
							ds.child("accountNo").getValue(String.class),
							ds.child("accountBalance").getValue(Double.class)
											));
					accounts.get(accounts.size() - 1).getTransactions()
							.addAll(getTransactionsFromCurrentAccount(customerID,accounts.get(accounts.size()-1).getAccountNo()));
				}
			}
			@Override
			public void onCancelled(@NonNull DatabaseError error)
			{
				Toast.makeText(context, "ERROR - Can't get customer's accounts from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",error.toString());
			}
		});

		return accounts;
	}

	//Todo: this function doesn't work, find out why
	public Customer getCustomerByID(String customerID)
	{
		Customer[] customer = new Customer[1];

		database.getReference(USERS).child(customerID).get()
				.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
			{
				@Override
				public void onComplete(@NonNull Task<DataSnapshot> task)
				{
					DataSnapshot ds = task.getResult();
					customer[0] = ds.getValue(Customer.class);
				}
			})
			 .addOnFailureListener(new OnFailureListener()
			 {
				 @Override
				 public void onFailure(@NonNull Exception e)
				 {
					 Toast.makeText(context, "ERROR - Can't get customer from DB", Toast.LENGTH_SHORT).show();
					 Log.d("DB_ERROR",e.toString());
				 }
			 });


//		database.getReference().child(USERS).child(customerID)
//				.addListenerForSingleValueEvent(new ValueEventListener()
//				{
//					@Override
//					public void onDataChange(@NonNull DataSnapshot snapshot)
//					{
//						customer[0] = snapshot.child(customerID).getValue(Customer.class);
//					}
//					@Override
//					public void onCancelled(@NonNull DatabaseError error)
//					{
//						Toast.makeText(context, "ERROR - Can't get customer from DB", Toast.LENGTH_SHORT).show();
//						Log.d("DB_ERROR",error.toString());
//					}
//				});
		return customer[0];
	}

	public ArrayList<Payee> getPayeesFromCurrentCustomer(long customerID)
	{
		ArrayList<Payee> payees = new ArrayList<>();
		database.getReference(USERS).child(String.valueOf(customerID)).child(KEY_PAYEES).get()
				.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
				{
					@Override
					public void onComplete(@NonNull Task<DataSnapshot> task)
					{
						for(DataSnapshot ds : task.getResult().getChildren())
							payees.add(ds.getValue(Payee.class));
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(context, "ERROR - Can't get payees from DB for customer", Toast.LENGTH_SHORT).show();
						Log.d("DB_ERROR",e.toString());
					}
				});

		return payees;
	}

	public ArrayList<Transaction> getTransactionsFromCurrentAccount(String customerID, String accountNo)
	{
		ArrayList<Transaction> transactions = new ArrayList<>();
		database.getReference("Accounts").child(customerID).child(accountNo)
				.child(KEY_TRANSACTIONS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					transactions.add(ds.getValue(Transaction.class));
			}
		})
		.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(context, "ERROR - Can't get transactions from DB for this account", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});

		return transactions;
	}
}
