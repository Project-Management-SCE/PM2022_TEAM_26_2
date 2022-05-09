package com.example.ymdbanking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.example.ymdbanking.model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

public class TransferActivity extends AppCompatActivity
{
	private Spinner spnSendingAccount;
	private EditText edtTransferAmount;
	private Spinner spnReceivingAccount;
	private Button btnConfirmTransfer;
	private Spinner spnReceivingCustomer;
	private long receivingProfileID;

	ArrayList<Customer> customersForTransfer;
	ArrayAdapter<Customer> customerAdapter;
//	ArrayList<Account> customerAccounts;
	ArrayAdapter<Account> customerAccountsAdapter;
	ArrayList<Account> accountsToTransfer;
	ArrayAdapter<Account> accountsToTransferAdapter;

	SessionManager sessionManager;
	Gson gson;
	String json;
	Customer customer;
	Admin admin;
	Clerk clerk;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);

		spnReceivingCustomer = findViewById(R.id.spn_select_profile_acc);
		spnSendingAccount = findViewById(R.id.spn_select_sending_acc);
		edtTransferAmount = findViewById(R.id.edt_transfer_amount);
		spnReceivingAccount = findViewById(R.id.spn_select_receiving_acc);
		btnConfirmTransfer = findViewById(R.id.btn_confirm_transfer);

		sessionManager = new SessionManager(getApplicationContext(),SessionManager.USER_SESSION);
		customer = sessionManager.getCustomerObjFromSession();

		btnConfirmTransfer.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				confirmTransfer();
			}
		});

		setValues();
		setAdapters();
	}

	/**
	 * method used to setup the values for the views and fields
	 */
	private void setValues()
	{
		customersForTransfer = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference("Users").get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					if(!ds.getKey().equals(customer.getId()))
						customersForTransfer.add(ds.getValue(Customer.class));

				//Setting adapter for customers list after pulling data from DB
				customerAdapter = new ArrayAdapter<Customer>(getApplicationContext(), android.R.layout.simple_spinner_item, customersForTransfer);
				customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnReceivingCustomer.setAdapter(customerAdapter);
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(getApplicationContext(), "ERROR - Can't get receiving customers from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});

		accountsToTransfer = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference("Accounts").get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					if(!ds.getKey().equals(customer.getId()))
						accountsToTransfer.add(ds.child(customer.getId()).getValue(Account.class));

				//Setting adapter for customers accounts after pulling data from DB
				accountsToTransferAdapter = new ArrayAdapter<Account>(getApplicationContext(), android.R.layout.simple_spinner_item, accountsToTransfer);
				accountsToTransferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnReceivingAccount.setAdapter(accountsToTransferAdapter);
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(getApplicationContext(), "ERROR - Can't get receiving customers accounts from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});
	}

	/**
	 * method that sets up the adapters
	 */
	private void setAdapters()
	{
//		ApplicationDB applicationDB = new ApplicationDB(getApplicationContext());
//		customers = applicationDB.getAllCustomersForTransfer(customer.getId());
//		accountsToTransfer = applicationDB.getAllAccountsForTransfer(customer.getId());
//		customerAccounts = applicationDB.getAccountsFromCurrentCustomer(customer.getId());
		//receivingProfileID = userProfile.getDbId();
//		customerAdapter = new ArrayAdapter<Customer>(getApplicationContext(), android.R.layout.simple_spinner_item, customers);
//		customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		customerAccountsAdapter = new ArrayAdapter<Account>(getApplicationContext(), android.R.layout.simple_spinner_item, customer.getAccounts());
		customerAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		accountsToTransferAdapter = new ArrayAdapter<Account>(getApplicationContext(), android.R.layout.simple_spinner_item, accountsToTransfer);
//		accountsToTransferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//		spnReceivingCustomer.setAdapter(customerAdapter);
		spnSendingAccount.setAdapter(customerAccountsAdapter);
//		spnReceivingAccount.setAdapter(accountsToTransferAdapter);
		//spnReceivingAccount.setSelection(1);
	}

	/**
	 * method that confirms the transfer
	 */
	private void confirmTransfer()
	{
		int receivingProfIndex = spnReceivingCustomer.getSelectedItemPosition();
		int receivingAccIndex = spnReceivingAccount.getSelectedItemPosition();
		boolean isNum = false;
		double transferAmount = 0;

		try
		{
			transferAmount = Double.parseDouble(edtTransferAmount.getText().toString());
			isNum = true;
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), "Please enter an amount to transfer", Toast.LENGTH_SHORT).show();
		}
		if (isNum)
		{
			//if (spnSendingAccount.getSelectedItemPosition() == receivingAccIndex)
			//{
			//    Toast.makeText(getActivity(), "You cannot make a transfer to the same account", Toast.LENGTH_SHORT).show();
			//}
			if (transferAmount < 0.01)
			{
				Toast.makeText(getApplicationContext(), "The minimum amount for a transfer is $0.01", Toast.LENGTH_SHORT).show();

			}
			else if (transferAmount > customer.getAccounts().get(spnSendingAccount.getSelectedItemPosition()).getAccountBalance())
			{
				Account acc = (Account) spnSendingAccount.getSelectedItem();
				Toast.makeText(getApplicationContext(), "The account," + " " + acc.toString() + " " +
				                              "does not have sufficient funds to make this transfer", Toast.LENGTH_LONG).show();
			}
			else
			{
				int sendingAccIndex = spnSendingAccount.getSelectedItemPosition();

				Account sendingAccount = (Account) spnSendingAccount.getItemAtPosition(sendingAccIndex);
				Account receivingAccount = (Account) spnReceivingAccount.getItemAtPosition(receivingAccIndex);
				Customer receivingCustomer = (Customer) spnReceivingCustomer.getItemAtPosition(receivingProfIndex);

				customer.addTransferTransaction(sendingAccount, receivingAccount, transferAmount);
				spnSendingAccount.setAdapter(customerAccountsAdapter);
				spnReceivingAccount.setAdapter(accountsToTransferAdapter);

				spnSendingAccount.setSelection(sendingAccIndex);
				spnReceivingAccount.setSelection(receivingAccIndex);

				ApplicationDB applicationDb = new ApplicationDB(getApplicationContext().getApplicationContext());

//                applicationDb.transferMoney(userProfile,sendingAccount,receivingProfile,receivingAccount,transferAmount);

				applicationDb.overwriteAccount(customer, sendingAccount);
				applicationDb.overwriteAccount(receivingCustomer, receivingAccount);

//				applicationDb.saveNewTransaction(customer, sendingAccount.getAccountNo(),
//						sendingAccount.getTransactions().get(
//								sendingAccount.getTransactions().size() - 1));
//				applicationDb.saveNewTransaction(receivingCustomer, receivingAccount.getAccountNo(),
//						receivingAccount.getTransactions().get(
//								receivingAccount.getTransactions().size() - 1));

				sessionManager.saveCustomerObjForSession(customer);

				Toast.makeText(getApplicationContext(), "Transfer of $" +
				                              String.format(Locale.getDefault(), "%.2f", transferAmount) +
				                              " successfully made", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void getAllCustomersForTransfer(String customerID)
	{
		customersForTransfer = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference("Accounts").get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					if(!ds.getKey().equals(customerID))
						customersForTransfer.add(ds.getValue(Customer.class));

				//Setting adapter for customers list after pulling data from DB
				customerAdapter = new ArrayAdapter<Customer>(getApplicationContext(), android.R.layout.simple_spinner_item, customersForTransfer);
				customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnReceivingCustomer.setAdapter(customerAdapter);
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(getApplicationContext(), "ERROR - Can't get receiving customers from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});
	}

	public void getAllAccountsForTransfer(String customerID)
	{
		accountsToTransfer = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference("Accounts").get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					if(!ds.getKey().equals(customerID))
						accountsToTransfer.add(ds.child(customerID).getValue(Account.class));

				//Setting adapter for customers accounts after pulling data from DB
				accountsToTransferAdapter = new ArrayAdapter<Account>(getApplicationContext(), android.R.layout.simple_spinner_item, accountsToTransfer);
				accountsToTransferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnReceivingAccount.setAdapter(accountsToTransferAdapter);
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(getApplicationContext(), "ERROR - Can't get receiving customers accounts from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});
	}
}

