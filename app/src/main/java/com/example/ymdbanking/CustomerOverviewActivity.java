package com.example.ymdbanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ymdbanking.adapters.CustomerAdapter;
import com.example.ymdbanking.adapters.ProfileAdapter;
import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.example.ymdbanking.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CustomerOverviewActivity extends AppCompatActivity
{
	private FloatingActionButton fab;
	private ListView lstProfiles;
	private TextView txtTitle;
	private TextView txtCustomerFullName;
	private TextView txtCustomerEmail;
	private TextView txtCustomerUsername;
	private Button btnCancel;
	private int selectedProfileIndex;
	private Clerk clerk;
	private Admin admin;
	private ArrayList<Customer> customers;
	private ArrayAdapter<Customer> customerAdapter;
	private Dialog customerDialog;
	private Button btnAssign;
	private SessionManager sessionManager;


	private View.OnClickListener assignUserClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if (view.getId() == btnCancel.getId())
			{
				customerDialog.dismiss();
				Toast.makeText(getApplicationContext(), "User Assign Canceled", Toast.LENGTH_SHORT).show();
			}
			else if (view.getId() == btnAssign.getId())
			{
				assignUserToClerk();
			}
		}
	};

	private void assignUserToClerk()
	{
			clerk.assignProfileToCustomer(customerAdapter.getItem(selectedProfileIndex),getApplicationContext());
			Toast.makeText(getApplicationContext(),"User has been assigned to you",Toast.LENGTH_SHORT).show();
			customerDialog.dismiss();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		customers = new ArrayList<>();
		setContentView(R.layout.activity_customers_overview);
		lstProfiles = findViewById(R.id.lst_profiles_overview);
		txtTitle = findViewById(R.id.txt_profile_fragment_title);

		setValues();
	}

	private void displayProfileDialog(int index)
	{
		customerDialog = new Dialog(getApplicationContext());
		customerDialog.setContentView(R.layout.assign_customer_dialog);
		customerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		customerDialog.setCanceledOnTouchOutside(true);
		customerDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				Toast.makeText(getApplicationContext(), "User assign canceled", Toast.LENGTH_SHORT).show();
			}
		});
		txtCustomerFullName = customerDialog.findViewById(R.id.txt_fullName_customer_dialog);
		txtCustomerEmail = customerDialog.findViewById(R.id.txt_email_customer_dialog);
		txtCustomerUsername = customerDialog.findViewById(R.id.txt_username_customer_dialog);
		txtCustomerFullName.setText(customerAdapter.getItem(index).getFullName());
		txtCustomerEmail.setText(customerAdapter.getItem(index).getEmail());
		txtCustomerUsername.setText(customerAdapter.getItem(index).getUsername());
		btnCancel = customerDialog.findViewById(R.id.btn_cancel_profile_dialog);
		btnAssign = customerDialog.findViewById(R.id.btn_assign_profile_dialog);
		btnCancel.setOnClickListener(assignUserClickListener);
		btnAssign.setOnClickListener(assignUserClickListener);
		customerDialog.show();
	}

	private void setValues()
	{
		selectedProfileIndex = 0;
		ApplicationDB applicationDB = new ApplicationDB(getApplicationContext().getApplicationContext());
		sessionManager = new SessionManager(getApplicationContext(),SessionManager.USER_SESSION);
		String sessionID = sessionManager.userSession.getString(SessionManager.KEY_TYPE_ID,null);
		if(sessionID.equals("1"))
		{
			admin = sessionManager.getAdminObjFromSession();
		}
		else if(sessionID.equals("2"))
		{
			clerk = sessionManager.getClerkObjFromSession();
		}
		getAllCustomers();
	}

	private void viewUser()
	{

	}

	public void getAllCustomers()
	{
		ArrayList<Customer> customers = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference("Users").get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
					if(ds.child("typeID").getValue(int.class) == 3)
						customers.add(ds.getValue(Customer.class));

				customerAdapter = new CustomerAdapter(getApplicationContext(),R.layout.lst_profile_row,customers);

				lstProfiles.setAdapter(customerAdapter);
				lstProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int i, long id)
					{
						selectedProfileIndex = i;
						displayProfileDialog(i);
					}
				});
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(getApplicationContext(), "ERROR - Can't get all customers from DB", Toast.LENGTH_SHORT).show();
				Log.d("DB_ERROR",e.toString());
			}
		});
	}

}
