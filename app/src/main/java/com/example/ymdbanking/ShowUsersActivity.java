package com.example.ymdbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.ymdbanking.adapters.ClerkAdapter;
import com.example.ymdbanking.adapters.ProfileAdapter;
import com.example.ymdbanking.model.Account;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowUsersActivity extends AppCompatActivity {

    private ListView usersList;
    private int selectedAccountIndex;
    private Admin admin;
    private ArrayList<Customer>customers;
    private SessionManager sessionManager;


    private Dialog clerkToUser;
    private Spinner selectClerk;
    private Button addBtn;
    private ArrayAdapter<Clerk> clerkAdapter;
    private ArrayList<Clerk> clerks;
    private ImageView cancelBtn;


    private View.OnClickListener clerkToUserClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == cancelBtn.getId())
            {
                clerkToUser.dismiss();
                Toast.makeText(ShowUsersActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
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
        
        
        selectedAccountIndex = 0;
        
        sessionManager = new SessionManager(ShowUsersActivity.this,SessionManager.USER_SESSION);
        admin = sessionManager.getAdminObjFromSession();

        customers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren())
                    if(ds.child("typeID").getValue(int.class)== 3)
                        customers.add(ds.getValue(Customer.class));

                ProfileAdapter adapter = new ProfileAdapter(ShowUsersActivity.this, R.layout.lst_profile_row, customers);
                usersList.setAdapter(adapter);

                usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedAccountIndex = i;
                        displayClerkToUserDialog();
//                        Intent intent = new Intent(ShowUsersActivity.this,AdminAccountsOverView.class);
//                        intent.putExtra("selecteduserid",customers.get(i).getId());
//                        startActivity(intent);

//                      viewUser();

                    }
                });
            }
        });


//
//        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                selectedAccountIndex = i;
//                viewAccount();
//            }
//        });




    }

    private void displayClerkToUserDialog() {
        
        clerkToUser = new Dialog(ShowUsersActivity.this);
        clerkToUser.setContentView(R.layout.add_clerk_to_user__dialog);
        
        clerkToUser.setCanceledOnTouchOutside(true);
        
        clerkToUser.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(ShowUsersActivity.this, "Adding Clerk has been cancelled", Toast.LENGTH_SHORT).show();
            }
        });


        cancelBtn = clerkToUser.findViewById(R.id.clerkToUser_cancelBtn);
        addBtn = clerkToUser.findViewById(R.id.add_clerkUser_btn);
        selectClerk = clerkToUser.findViewById(R.id.spn_select_clerk);

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

        selectClerk.setAdapter(clerkAdapter);


        cancelBtn.setOnClickListener(clerkToUserClickListener);
        addBtn.setOnClickListener(clerkToUserClickListener);

        clerkToUser.show();

    }

    private void addClerkToUser() {
    }


}