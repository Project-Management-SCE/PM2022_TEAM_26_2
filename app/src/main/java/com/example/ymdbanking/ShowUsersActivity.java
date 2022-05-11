package com.example.ymdbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ymdbanking.adapters.ProfileAdapter;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowUsersActivity extends AppCompatActivity {

    private ListView usersList;
    private int selectedCustomerIndex;
    private Admin admin;
    private Clerk clerk;
    private ArrayList<Customer>customers;
    private SessionManager sessionManager;


    private Dialog dlgClerkToUser;
    private Spinner spnSelectClerk;
    private Button addBtn;
    private ArrayAdapter<Clerk> clerkAdapter;
    private ArrayList<Clerk> clerks;
    private ImageView cancelBtn;
    private String sessionID;


    private View.OnClickListener clerkToUserClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == cancelBtn.getId())
            {
                dlgClerkToUser.dismiss();
                Toast.makeText(ShowUsersActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == addBtn.getId())
            {
                addClerkToUser();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        usersList = findViewById(R.id.lst_users);

        selectedCustomerIndex = 0;
        
        sessionManager = new SessionManager(ShowUsersActivity.this,SessionManager.USER_SESSION);
        sessionID = sessionManager.userSession.getString(SessionManager.KEY_TYPE_ID,null);
        if(sessionID.equals("1"))
            admin = sessionManager.getAdminObjFromSession();
        else if(sessionID.equals("2"))
            clerk = sessionManager.getClerkObjFromSession();

        setValues();
    }

    private void setValues()
    {
        ArrayList<Customer> tempCustomers = new ArrayList<>();
        if(sessionID.equals("2"))
            tempCustomers = getClerkCustomers();
        customers = new ArrayList<>();
        ArrayList<Customer> finalTempCustomers = tempCustomers;
        FirebaseDatabase.getInstance().getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren())
                    if(ds.child("typeID").getValue(int.class) == 3)
                    {
                        boolean exists = false;
                        for(Customer customer : finalTempCustomers)
                            if(customer.getId().equals(ds.getValue(Customer.class).getId()))
                                exists = true;

                        if(!exists)
                            customers.add(ds.getValue(Customer.class));
                    }

                ProfileAdapter adapter = new ProfileAdapter(ShowUsersActivity.this, R.layout.lst_profile_row, customers);
                usersList.setAdapter(adapter);

                usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedCustomerIndex = i;
                        displayClerkToUserDialog();
//                        Intent intent = new Intent(ShowUsersActivity.this,AdminAccountsOverView.class);
//                        intent.putExtra("selecteduserid",customers.get(i).getId());
//                        startActivity(intent);

//                      viewUser();
                    }
                });
            }
        });
    }

    private ArrayList<Customer> getClerkCustomers()
    {
        ArrayList<Customer> customers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("ClerkCustomers").child(clerk.getId())
            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
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

            }
        });
        return customers;
    }

    private void displayClerkToUserDialog()
    {
        dlgClerkToUser = new Dialog(ShowUsersActivity.this);
        dlgClerkToUser.setContentView(R.layout.add_clerk_to_user_dialog);
        dlgClerkToUser.setCanceledOnTouchOutside(true);
        
        dlgClerkToUser.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(ShowUsersActivity.this, "Adding Clerk has been cancelled", Toast.LENGTH_SHORT).show();
            }
        });


        cancelBtn = dlgClerkToUser.findViewById(R.id.clerkToUser_cancelBtn);
        addBtn = dlgClerkToUser.findViewById(R.id.add_clerkUser_btn);
        spnSelectClerk = dlgClerkToUser.findViewById(R.id.spn_select_clerk);

        clerks = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren())
                    if(ds.child("typeID").getValue(int.class)== 2)
                        clerks.add(ds.getValue(Clerk.class));


            }
        });
        clerkAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clerks);
        clerkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectClerk.setAdapter(clerkAdapter);

        cancelBtn.setOnClickListener(clerkToUserClickListener);
        addBtn.setOnClickListener(clerkToUserClickListener);

        dlgClerkToUser.show();

    }

    private void addClerkToUser()
    {
        clerk.assignProfileToCustomer(((Customer) usersList.getAdapter().getItem(selectedCustomerIndex)),getApplicationContext());
        Toast.makeText(getApplicationContext(),"User has been assigned to you",Toast.LENGTH_SHORT).show();
        dlgClerkToUser.dismiss();
        setValues();
    }
}